package cn.nihility.api.service.impl;

import cn.nihility.api.dto.Oauth2Response;
import cn.nihility.api.properties.AuthenticationProperties;
import cn.nihility.api.service.IOauth2Service;
import cn.nihility.api.service.ISessionService;
import cn.nihility.api.service.ITokenService;
import cn.nihility.common.constant.AuthConstant;
import cn.nihility.common.constant.Oauth2Constant;
import cn.nihility.common.constant.Oauth2ErrorEnum;
import cn.nihility.common.entity.AuthenticateSession;
import cn.nihility.common.entity.AuthenticationToken;
import cn.nihility.common.exception.AuthenticationException;
import cn.nihility.common.http.RequestContextHolder;
import cn.nihility.common.util.AuthenticationUtils;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.common.util.Oauth2Utils;
import cn.nihility.common.util.UuidUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/18 11:27
 */
@Service
public class Oauth2ServiceImpl implements IOauth2Service {

    private static final Logger log = LoggerFactory.getLogger(Oauth2ServiceImpl.class);

    private AuthenticationProperties authenticationProperties;
    private ISessionService sessionService;
    private ITokenService tokenService;

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
        String grantType = request.getParameter(Oauth2Constant.GRANT_TYPE);
        String redirectUri = request.getParameter(Oauth2Constant.REDIRECT_URI);

        log.info("code grant code [{}], grant_type [{}], redirect_uri [{}]", code, grantType, redirectUri);

        AuthenticationToken aToken = tokenService.getTokenById(code);
        if (StringUtils.isBlank(code) || null == aToken) {
            log.error("请求 token 为空");
            throw new AuthenticationException("请求 token 不可为空");
        }
        AuthenticateSession authSession = sessionService.getSessionById(aToken.getSessionId(), response);

        Oauth2Response oResponse = new Oauth2Response();
        AuthenticationToken accessToken = AuthenticationUtils.createToken(authSession.getSessionId(), Oauth2Constant.PROTOCOL,
            Oauth2Constant.ACCESS_TOKEN, AuthenticationUtils.tokenId(Oauth2Constant.ACCESS_TOKEN_PREFIX));
        AuthenticationToken refreshToken = AuthenticationUtils.createToken(authSession.getSessionId(), Oauth2Constant.PROTOCOL,
            Oauth2Constant.REFRESH_TOKEN, AuthenticationUtils.tokenId(Oauth2Constant.REFRESH_TOKEN_PREFIX));

        refreshToken.setRefTokenId(accessToken.getTokenId());

        if (Oauth2Constant.GRANT_AUTHORIZATION_CODE_TYPE.equals(grantType)) {
            // 创建新的，删除 code
            tokenService.deleteToken(code);
        } else if (Oauth2Constant.GRANT_REFRESH_TOKEN_GRANT_TYPE.equals(grantType)) {
            // 生成新的，删除老的
            AuthenticationToken oldRefreshToken = tokenService.getTokenById(code);
            if (oldRefreshToken == null) {
                throw new AuthenticationException("refresh_token 不存在/已失效");
            }

            tokenService.deleteToken(oldRefreshToken.getRefTokenId());
            tokenService.deleteToken(oldRefreshToken.getTokenId());
        }

        tokenService.createToken(accessToken);
        tokenService.createToken(refreshToken);

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
        String tokenId;
        AuthenticationToken aToken;
        // 隐式授权模式
        if (Oauth2Constant.RESPONSE_TYPE_IMPLICIT_VALUE.equals(responseType)) {
            tokenId = AuthenticationUtils.tokenId(Oauth2Constant.ACCESS_TOKEN_PREFIX);
            params.put(Oauth2Constant.ACCESS_TOKEN, tokenId);
            params.put(Oauth2Constant.IMPLICIT_EXPIRES_IN, Long.toString(AuthConstant.TOKEN_TTL));
            params.put(Oauth2Constant.STATE, Oauth2Utils.state(request));
            params.put(Oauth2Constant.TOKEN_TYPE, Oauth2Constant.TOKEN_TYPE_VALUE);

            aToken = AuthenticationUtils.createToken(session.getSessionId(), Oauth2Constant.PROTOCOL,
                Oauth2Constant.ACCESS_TOKEN, tokenId);
        } else {
            tokenId = UuidUtils.jdkUUID();
            aToken = AuthenticationUtils.createToken(session.getSessionId(), Oauth2Constant.PROTOCOL,
                Oauth2Constant.RESPONSE_TYPE_CODE_VALUE, tokenId);
        }

        params.put(Oauth2Constant.RESPONSE_TYPE_CODE_VALUE, tokenId);
        tokenService.createToken(aToken);

        return HttpRequestUtils.addUrlParams(redirectUri, params);

    }

}
