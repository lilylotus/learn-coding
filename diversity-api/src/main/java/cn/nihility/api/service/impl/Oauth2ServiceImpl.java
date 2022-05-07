package cn.nihility.api.service.impl;

import cn.nihility.api.dto.Oauth2Response;
import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.api.properties.AuthenticationProperties;
import cn.nihility.api.service.IOauth2Service;
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

/**
 * @author nihility
 * @date 2022/02/18 11:27
 */
@Service
public class Oauth2ServiceImpl implements IOauth2Service {

    private static final Logger log = LoggerFactory.getLogger(Oauth2ServiceImpl.class);

    private final AuthenticationProperties authenticationProperties;
    private final ISessionService sessionService;
    private final ITokenService tokenService;

    public Oauth2ServiceImpl(AuthenticationProperties authenticationProperties,
                             ISessionService sessionService,
                             ITokenService tokenService) {
        this.authenticationProperties = authenticationProperties;
        this.sessionService = sessionService;
        this.tokenService = tokenService;
    }

    /**
     * GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
     */
    @Override
    public String authorize(HttpServletRequest request, HttpServletResponse response) {

        String state = Oauth2Utils.state(request);
        String clientId = Oauth2Utils.clientId(request);
        String responseType = Oauth2Utils.codeGrantResponseType(request);
        String redirectUri = Oauth2Utils.redirectUri(request);
        if (StringUtils.isBlank(redirectUri)) {
            throw new AuthenticationException("redirect_uri 参数不可为空");
        }
        if (StringUtils.isBlank(clientId)) {
            return Oauth2Utils.buildErrorRedirectUrl(redirectUri, Oauth2ErrorEnum.INVALID_REQUEST.getCode(),
                "client_id 不可为空", state);
        }
        log.info("oauth 2.0 authorize response_type [{}]", responseType);
        if (!(Oauth2Constant.RESPONSE_TYPE_CODE_VALUE.equals(responseType) ||
            Oauth2Constant.RESPONSE_TYPE_IMPLICIT_VALUE.equals(responseType))) {
            return Oauth2Utils.buildErrorRedirectUrl(redirectUri,
                Oauth2ErrorEnum.UNSUPPORTED_RESPONSE_TYPE, state);
        }

        String domain = HttpRequestUtils.getOriginRequestUrl(request);
        String redirectUrl = HttpRequestUtils.addUrlParams(domain + "/auth/oauth/authorize",
            HttpRequestUtils.paramsToMap(request));
        String frontRedirectUrl = domain + authenticationProperties.getAuthLoginPrefix() + "?redirect=" +
            HttpRequestUtils.urlEncode(redirectUrl) + "&clientId=" + clientId;

        if (log.isDebugEnabled()) {
            log.debug("oauth authorize domain [{}]", domain);
            log.debug("oauth authorize redirectUrl [{}]", redirectUrl);
            log.debug("oauth authorize frontRedirectUrl [{}]", frontRedirectUrl);
        }

        AuthenticateSession authSession = RequestContextHolder.getContext().getAuthSession();
        if (authSession != null) {
            return authenticateOkRedirect(request, authSession);
        }

        return frontRedirectUrl;

    }

    @Override
    public Oauth2Response createCodeGrantToken(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter(Oauth2Constant.RESPONSE_TYPE_CODE_VALUE);
        String refreshTokenValue = request.getParameter(Oauth2Constant.REFRESH_TOKEN);
        String grantType = request.getParameter(Oauth2Constant.GRANT_TYPE);
        String redirectUri = request.getParameter(Oauth2Constant.REDIRECT_URI);
        String scope = Oauth2Utils.scope(request);

        log.info("code grant code [{}], grant_type [{}], scope [{}], redirect_uri [{}]", code, grantType, scope, redirectUri);

        Oauth2Response oResponse = new Oauth2Response();
        AuthenticateSession authSession = null;

        if (Oauth2Constant.GRANT_AUTHORIZATION_CODE_TYPE.equals(grantType)) {
            if (StringUtils.isBlank(code)) {
                log.error("oauth2 code 换 token 请求 code 为空");
                throw new AuthenticationException("获取 access_token 的 code 不可为空");
            }
            AuthenticationToken codeToken = tokenService.getOauthToken(code);
            if (codeToken == null) {
                log.error("oauth2 code 换 token 请求 code 不存在或已失效");
                throw new AuthenticationException("获取 access_token 的 code 不存在或已失效");
            }
            // 创建新的，删除 code
            tokenService.deleteOauthToken(code);
            authSession = sessionService.getSessionById(codeToken.getSessionId(), response);

        } else if (Oauth2Constant.GRANT_REFRESH_TOKEN_GRANT_TYPE.equals(grantType)) {
            // grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
            if (StringUtils.isBlank(refreshTokenValue)) {
                log.error("oauth2 刷新 access_token 的 refresh_token 为空");
                throw new AuthenticationException("刷新 access_token 的 refresh_token 不可为空");
            }
            AuthenticationToken oldRefreshToken = tokenService.getOauthToken(refreshTokenValue);
            if (oldRefreshToken == null) {
                log.error("oauth2 刷新 access_token 的 refresh_token 不存在或已失效");
                throw new AuthenticationException("刷新 access_token 的 refresh_token 不存在或已失效");
            }
            // 生成新的，删除老的
            authSession = sessionService.getSessionById(oldRefreshToken.getSessionId(), response);
            tokenService.deleteOauthToken(oldRefreshToken.getRefTokenId());
            tokenService.deleteOauthToken(oldRefreshToken.getTokenId());
        }

        if (null == authSession) {
            log.error("会话已过期");
            throw new AuthenticationException("会话已过期");
        }

        AuthenticationToken accessToken = AuthenticationUtils.createToken(authSession.getSessionId(), Oauth2Constant.PROTOCOL,
            Oauth2Constant.ACCESS_TOKEN, Oauth2Constant.ACCESS_TOKEN_PREFIX);
        AuthenticationToken refreshToken = AuthenticationUtils.createToken(authSession.getSessionId(), Oauth2Constant.PROTOCOL,
            Oauth2Constant.REFRESH_TOKEN, Oauth2Constant.REFRESH_TOKEN_PREFIX);
        refreshToken.setRefTokenId(accessToken.getTokenId());

        authSession.addToken(accessToken);
        authSession.addToken(refreshToken);
        sessionService.updateSession(authSession);

        tokenService.createOauthToken(accessToken);
        tokenService.createOauthToken(refreshToken);

        oResponse.setRefreshToken(refreshToken.getTokenId());
        oResponse.setAccessToken(accessToken.getTokenId());
        oResponse.setExpiresIn(accessToken.getTtl());
        oResponse.setTokenType(Oauth2Constant.TOKEN_TYPE_VALUE);

        return oResponse;
    }

    private String authenticateOkRedirect(HttpServletRequest request, AuthenticateSession session) {

        String responseType = Oauth2Utils.codeGrantResponseType(request);
        String redirectUri = Oauth2Utils.redirectUri(request);

        Map<String, String> params = new HashMap<>(8);
        AuthenticationToken aToken;
        // 隐式授权模式
        if (Oauth2Constant.RESPONSE_TYPE_IMPLICIT_VALUE.equals(responseType)) {
            aToken = AuthenticationUtils.createToken(session.getSessionId(), Oauth2Constant.PROTOCOL,
                Oauth2Constant.ACCESS_TOKEN, Oauth2Constant.ACCESS_TOKEN_PREFIX);

            params.put(Oauth2Constant.ACCESS_TOKEN, aToken.getTokenId());
            params.put(Oauth2Constant.IMPLICIT_EXPIRES_IN, Long.toString(AuthConstant.TOKEN_TTL));
            params.put(Oauth2Constant.STATE, Oauth2Utils.state(request));
            params.put(Oauth2Constant.TOKEN_TYPE, Oauth2Constant.TOKEN_TYPE_VALUE);
        } else {
            aToken = AuthenticationUtils.createToken(session.getSessionId(), Oauth2Constant.PROTOCOL,
                Oauth2Constant.RESPONSE_TYPE_CODE_VALUE, Oauth2Constant.CODE_TOKEN_PREFIX);

            params.put(Oauth2Constant.RESPONSE_TYPE_CODE_VALUE, aToken.getTokenId());
        }

        tokenService.createOauthToken(aToken);

        return HttpRequestUtils.addUrlParams(redirectUri, params);

    }

    @Override
    public Map<String, Object> userInfo(HttpServletRequest request, HttpServletResponse response) {
        String token = HttpRequestUtils.bearerTokenValue(request.getHeader(AuthConstant.AUTHORIZATION_KEY));
        log.info("token [{}]", token);

        if (StringUtils.isBlank(token)) {
            log.error("oauth2 获取用户信息 token 不可为空");
            throw new AuthenticationException("oauth2 获取用户信息 token 不可为空");
        }
        AuthenticationToken aToken = tokenService.getOauthToken(token);
        if (aToken == null) {
            log.error("oauth2 获取用户信息 token 不存在或已失效");
            throw new AuthenticationException("oauth2 获取用户信息 token 不存在或已失效");
        }
        AuthenticateSession authSession = sessionService.getSessionById(aToken.getSessionId(), response);
        if (authSession == null) {
            log.error("用户未认证，认证 session 不存在");
            throw new AuthenticationException("用户未认证，认证 session 不存在");
        }
        return Optional.ofNullable(authSession.getUserAttributes()).orElse(Collections.emptyMap());
    }

    @Override
    public Map<String, Object> rec(HttpServletRequest request, HttpServletResponse response) {

        String code = request.getParameter(Oauth2Constant.RESPONSE_TYPE_CODE_VALUE);
        if (StringUtils.isBlank(code)) {
            log.error("oauth2 认证 code 不可为空");
            throw new AuthenticationException("oauth2 认证 code 不可为空");
        }

        // code 换 token
        log.info("oauth2 callback code [{}]", code);

        // 依据 code 获取 access token
        String url = "http://127.0.0.1:30010/auth/oauth/token";
        String userInfoUrl = "http://127.0.0.1:30010/auth/oauth/user-info";
        Map<String, String> params = new HashMap<>(8);
        params.put(Oauth2Constant.CLIENT_ID, "OAuth2.0 Client Id");
        params.put(Oauth2Constant.CLIENT_SECRET, UuidUtils.jdkUUID());
        params.put(Oauth2Constant.SCOPE, "all");
        params.put(Oauth2Constant.REDIRECT_URI, "http://127.0.0.1:30010/auth/oauth/user-info?param1=中文&param2=English");
        params.put(Oauth2Constant.GRANT_TYPE, Oauth2Constant.GRANT_AUTHORIZATION_CODE_TYPE);
        params.put("code", code);

        Oauth2Response oauth2Response = HttpClientUtils.executeFormRequest(url, RequestMethodEnum.POST, params, Oauth2Response.class);
        log.info("oauth2 获取 access_token 响应数据 [{}]", JacksonUtils.toJsonString(oauth2Response));
        String accessToken = null;
        if (oauth2Response == null) {
            log.error("oauth2 获取 access_token 响应为空");
        } else {
            accessToken = oauth2Response.getAccessToken();
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

        log.info("用户信息 [{}]", userInfoResult);

        return userInfoResult;
    }

}
