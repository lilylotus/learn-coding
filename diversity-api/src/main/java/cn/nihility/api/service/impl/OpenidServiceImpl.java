package cn.nihility.api.service.impl;

import cn.nihility.api.dto.OidcTokenDto;
import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.api.properties.AuthenticationProperties;
import cn.nihility.api.service.IOpenidService;
import cn.nihility.api.service.ISessionService;
import cn.nihility.api.service.ITokenService;
import cn.nihility.common.constant.*;
import cn.nihility.common.entity.AuthenticateSession;
import cn.nihility.common.entity.AuthenticationToken;
import cn.nihility.common.exception.AuthenticationException;
import cn.nihility.common.http.RequestContextHolder;
import cn.nihility.common.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OpenidServiceImpl implements IOpenidService {

    private static final Logger logger = LoggerFactory.getLogger(OpenidServiceImpl.class);

    private final AuthenticationProperties authenticationProperties;
    private final ITokenService tokenService;
    private final ISessionService sessionService;

    public OpenidServiceImpl(AuthenticationProperties authenticationProperties,
                             ITokenService tokenService,
                             ISessionService sessionService) {
        this.authenticationProperties = authenticationProperties;
        this.tokenService = tokenService;
        this.sessionService = sessionService;
    }

    /**
     * GET /authorize?
     * response_type=code
     * &scope=openid%20profile%20email
     * &client_id=s6BhdRkqt3
     * &state=af0ifjsldkj
     * &redirect_uri=https%3A%2F%2Fclient.example.org%2Fcb HTTP/1.1
     */
    @Override
    public String authorize(HttpServletRequest request, HttpServletResponse response) {

        String validateResult = OidcUtils.validateAuthorizeParams(request);
        if (null != validateResult) {
            return validateResult;
        }

        String clientId = OidcUtils.obtainClientId(request);
        String domain = HttpRequestUtils.getOriginRequestUrl(request);
        String redirectUrl = HttpRequestUtils.addUrlParams(domain + "/auth/oidc/authorize",
            HttpRequestUtils.paramsToMap(request));
        String frontRedirectUrl = domain + authenticationProperties.getAuthLoginPrefix() + "?redirect=" +
            HttpRequestUtils.urlEncode(redirectUrl) + "&clientId=" + clientId;

        if (logger.isDebugEnabled()) {
            logger.debug("oidc authorize domain [{}]", domain);
            logger.debug("oidc authorize redirectUrl [{}]", redirectUrl);
            logger.debug("oidc authorize frontRedirectUrl [{}]", frontRedirectUrl);
        }

        AuthenticateSession authSession = RequestContextHolder.getContext().getAuthSession();
        return authSession == null ? frontRedirectUrl : authenticateOkRedirect(request, authSession);

    }

    private String authenticateOkRedirect(HttpServletRequest request, AuthenticateSession session) {

        String responseType = OidcUtils.obtainResponseType(request);
        String state = OidcUtils.obtainState(request);
        String redirectUri = OidcUtils.obtainRedirectUri(request);

        AuthenticationToken token;
        Map<String, String> param = new HashMap<>();
        param.put(OidcConstant.STATE_FIELD, state);

        if (OidcConstant.AUTHORIZATION_CODE_VALUE.equals(responseType)) {
            // 302 Location: https://client.example.com/cb?code=SplxlOBeZQQYbYS6WxSbIA&state=xyz

            token = AuthenticationUtils.createToken(session.getSessionId(), OidcConstant.PROTOCOL,
                OidcConstant.AUTHORIZE_TOKEN_TYPE, OidcConstant.CODE_TOKEN_PREFIX, OidcConstant.AUTHORIZE_CODE_TTL);

            param.put(OidcConstant.AUTHORIZATION_CODE_VALUE, token.getTokenId());
        } else if (OidcConstant.IMPLICIT_GRANT_VALUE.equals(responseType)) {
            // 302 Location: https://client.example.org/cb#access_token=SlAV32hkKG&token_type=bearer&id_token=eyJ0&expires_in=3600&state=af0ifjsldkj

            token = AuthenticationUtils.createToken(session.getSessionId(), OidcConstant.PROTOCOL,
                OidcConstant.AUTHORIZE_IMPLICIT_TOKEN_TYPE, OidcConstant.ACCESS_TOKEN_PREFIX, OidcConstant.ACCESS_TOKEN_TTL);

            JwtUtils.JwtHolder jwtHolder = createUserJwt(session.getUserAttributes(), token.getTokenId());
            tokenService.createJwt(token.getTokenId(), jwtHolder);

            param.put(OidcConstant.RESPONSE_ACCESS_TOKEN_FIELD, token.getTokenId());
            param.put(OidcConstant.RESPONSE_TOKEN_TYPE_FIELD, OidcConstant.TOKEN_TYPE_VALUE);
            param.put(OidcConstant.RESPONSE_ID_TOKEN_FIELD, jwtHolder.getAccessToken());
            param.put(OidcConstant.RESPONSE_EXPIRES_IN_FIELD, Long.toString(OidcConstant.ACCESS_TOKEN_TTL));
        } else {
            return OidcUtils.buildErrorRedirectUrl(redirectUri, Oauth2ErrorEnum.UNSUPPORTED_RESPONSE_TYPE.getCode(),
                "不支持的 response_type 类型", state);
        }

        tokenService.createOidcToken(token);

        return HttpRequestUtils.addUrlParams(redirectUri, param);
    }

    private JwtUtils.JwtHolder createUserJwt(Map<String, Object> userAttributes, String id) {
        final Map<String, String> attrs = new HashMap<>();
        if (null != userAttributes) {
            userAttributes.forEach((k, v) -> attrs.put(k, String.valueOf(v)));
        }
        return JwtUtils.createJwtToken(id, OidcConstant.ACCESS_TOKEN_TTL, attrs);
    }

    /**
     * grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
     */
    @Override
    public OidcTokenDto codeConvertToken(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter(OidcConstant.TOKEN_REQUEST_CODE_FIELD);
        String grantType = request.getParameter(OidcConstant.TOKEN_REQUEST_GRANT_TYPE_FIELD);
        String redirectUri = request.getParameter(OidcConstant.REDIRECT_URI_FIELD);
        String clientId = request.getParameter(OidcConstant.CLIENT_ID_FIELD);

        logger.info("oidc token grant_type [{}], code [{}], redirect_uri [{}], client_id [{}]",
            grantType, code, redirectUri, clientId);

        if (StringUtils.isBlank(code)) {
            throw new AuthenticationException("参数 code 不可为空");
        }
        if (StringUtils.isBlank(grantType)) {
            throw new AuthenticationException("参数 grant_type 不可为空");
        }
        if (StringUtils.isBlank(clientId)) {
            throw new AuthenticationException("参数 client_id 不可为空");
        }
        if (StringUtils.isBlank(redirectUri)) {
            throw new AuthenticationException("参数 redirect_uri 不可为空");
        }

        OidcTokenDto oidcTokenDto = new OidcTokenDto();
        AuthenticateSession authSession = null;

        if (OidcConstant.TOKEN_REQUEST_GRANT_CODE_TYPE_VALUE.equals(grantType)) {
            if (StringUtils.isBlank(code)) {
                logger.error("oidc code 换 token 请求 code 为空");
                throw new AuthenticationException("获取 access_token 的 code 不可为空");
            }
            AuthenticationToken codeToken = tokenService.getOidcToken(code);
            if (codeToken == null) {
                logger.error("oidc code 换 token 请求 code 不存在或已失效");
                throw new AuthenticationException("获取 access_token 的 code 不存在或已失效");
            }
            // 创建新的，删除 code
            tokenService.deleteOidcToken(code);
            authSession = sessionService.getSessionById(codeToken.getSessionId(), response);

        } else if (OidcConstant.TOKEN_REQUEST_GRANT_REFRESH_TYPE_VALUE.equals(grantType)) {
            // grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
            String rToken = request.getParameter(OidcConstant.TOKEN_REQUEST_GRANT_REFRESH_TYPE_VALUE);
            if (StringUtils.isBlank(rToken)) {
                logger.error("oidc 刷新 access_token 的 refresh_token 为空");
                throw new AuthenticationException("刷新 access_token 的 refresh_token 不可为空");
            }
            AuthenticationToken oldRefreshToken = tokenService.getOauthToken(rToken);
            if (oldRefreshToken == null) {
                logger.error("oidc 刷新 access_token 的 refresh_token 不存在或已失效");
                throw new AuthenticationException("刷新 access_token 的 refresh_token 不存在或已失效");
            }
            // 生成新的，删除老的
            authSession = sessionService.getSessionById(oldRefreshToken.getSessionId(), response);
            tokenService.deleteOidcToken(oldRefreshToken.getRefTokenId());
            tokenService.deleteOidcToken(oldRefreshToken.getTokenId());
        }

        if (null == authSession) {
            logger.error("会话已过期");
            throw new AuthenticationException("会话已过期");
        }

        AuthenticationToken accessToken = AuthenticationUtils.createToken(authSession.getSessionId(), OidcConstant.PROTOCOL,
            OidcConstant.TOKEN_TOKEN_TYPE, OidcConstant.ACCESS_TOKEN_PREFIX, OidcConstant.ACCESS_TOKEN_TTL);
        AuthenticationToken refreshToken = AuthenticationUtils.createToken(authSession.getSessionId(), OidcConstant.PROTOCOL,
            OidcConstant.TOKEN_TOKEN_TYPE, OidcConstant.REFRESH_TOKEN_PREFIX, OidcConstant.REFRESH_TOKEN_TTL);
        JwtUtils.JwtHolder jwtHolder = createUserJwt(authSession.getUserAttributes(), accessToken.getTokenId());
        refreshToken.setRefTokenId(accessToken.getTokenId());

        authSession.addToken(accessToken);
        authSession.addToken(refreshToken);
        sessionService.updateSession(authSession);

        tokenService.createJwt(accessToken.getTokenId(), jwtHolder);
        tokenService.createOidcToken(accessToken);
        tokenService.createOidcToken(refreshToken);

        oidcTokenDto.setRefreshToken(refreshToken.getTokenId());
        oidcTokenDto.setAccessToken(accessToken.getTokenId());
        oidcTokenDto.setIdToken(jwtHolder.getAccessToken());
        oidcTokenDto.setExpiresIn(accessToken.getTtl());
        oidcTokenDto.setTokenType(OidcConstant.TOKEN_TYPE_VALUE);

        return oidcTokenDto;
    }

    @Override
    public Map<String, Object> userInfo(HttpServletRequest request, HttpServletResponse response) {
        String token = HttpRequestUtils.bearerTokenValue(request.getHeader(AuthConstant.AUTHORIZATION_KEY));
        logger.info("oidc access_token [{}]", token);

        if (StringUtils.isBlank(token)) {
            logger.error("oidc 获取用户信息 access_token 不可为空");
            throw new AuthenticationException("oidc 获取用户信息 access_token 不可为空");
        }
        AuthenticationToken aToken = tokenService.getOidcToken(token);
        if (aToken == null) {
            logger.error("oidc 获取用户信息 access_token 不存在或已失效");
            throw new AuthenticationException("oidc 获取用户信息 access_token 不存在或已失效");
        }
        AuthenticateSession authSession = sessionService.getSessionById(aToken.getSessionId(), response);
        if (authSession == null) {
            logger.error("用户未认证，认证 session 不存在");
            throw new AuthenticationException("用户未认证，认证 session 不存在");
        }
        return Optional.ofNullable(authSession.getUserAttributes()).orElse(Collections.emptyMap());
    }

    @Override
    public Map<String, Object> callback(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter(OidcConstant.TOKEN_REQUEST_CODE_FIELD);
        if (StringUtils.isBlank(code)) {
            logger.error("oidc token request code 不可为空");
            throw new AuthenticationException("oidc token request code 不可为空");
        }

        // code 换 token
        logger.info("oidc token request callback code [{}]", code);

        // 依据 code 获取 access token
        String url = "http://127.0.0.1:30010/auth/oidc/token";
        String userInfoUrl = "http://127.0.0.1:30010/auth/oidc/user-info";
        Map<String, String> params = new HashMap<>(8);
        params.put(OidcConstant.CLIENT_ID_FIELD, "oidc Client Id");
        params.put(OidcConstant.SCOPE_FIELD, OidcConstant.SCOPE_VALUE);
        params.put(OidcConstant.REDIRECT_URI_FIELD, "http://127.0.0.1:30010/auth/oidc/user-info?param1=中文&param2=English");
        params.put(OidcConstant.TOKEN_REQUEST_GRANT_TYPE_FIELD, OidcConstant.TOKEN_REQUEST_GRANT_CODE_TYPE_VALUE);
        params.put(OidcConstant.TOKEN_REQUEST_CODE_FIELD, code);

        OidcTokenDto oidcToken = HttpClientUtils.executeFormRequest(url, RequestMethodEnum.POST, params, OidcTokenDto.class);
        logger.info("oidc 获取 access_token 响应数据 [{}]", JacksonUtils.toJsonString(oidcToken));
        String accessToken = null;
        if (oidcToken == null) {
            logger.error("oidc 获取 access_token 响应为空");
        } else {
            accessToken = oidcToken.getAccessToken();
        }
        if (StringUtils.isBlank(accessToken)) {
            throw new HttpRequestException(HttpStatus.UNAUTHORIZED, UnifyResultUtils.failure("获取 access_token 失败"));
        }

        // 依据 access token 获取用户信息
        HttpGet get = new HttpGet(userInfoUrl);
        HttpRequestUtils.addHeader(get, Constant.AUTHENTICATION_TOKEN_KEY,
            Constant.AUTHENTICATION_BEARER_TOKEN_PREFIX + accessToken);
        @SuppressWarnings("unchecked")
        Map<String, Object> userInfoResult = HttpClientUtils.executeHttpRequest(get, Map.class);

        logger.info("用户信息 [{}]", JacksonUtils.toJsonString(userInfoResult));

        userInfoResult.put("token", oidcToken);

        return userInfoResult;
    }
}
