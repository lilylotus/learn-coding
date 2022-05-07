package cn.nihility.api.service.impl;

import cn.nihility.api.service.IAuthenticationService;
import cn.nihility.api.service.ISessionService;
import cn.nihility.api.service.ITokenService;
import cn.nihility.api.util.CookieUtils;
import cn.nihility.common.constant.AuthConstant;
import cn.nihility.common.entity.AuthenticateSession;
import cn.nihility.common.entity.AuthenticationToken;
import cn.nihility.common.entity.UserInfo;
import cn.nihility.common.exception.AuthenticationException;
import cn.nihility.common.http.RequestContextHolder;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.common.util.UuidUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author nihility
 * @date 2022/02/21 10:26
 */
@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final ISessionService sessionService;
    private final ITokenService tokenService;

    public AuthenticationServiceImpl(ISessionService sessionService, ITokenService tokenService) {
        this.sessionService = sessionService;
        this.tokenService = tokenService;
    }

    @Override
    public String auth(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> params = HttpRequestUtils.paramsToMap(request);
        String redirect = params.get(AuthConstant.REDIRECT_TAG);
        log.info("redirect url [{}]", redirect);

        UserInfo info = new UserInfo();
        String uid = UuidUtils.jdkUUID();
        info.setUserId(uid);
        info.setUserName(params.get("username"));

        Map<String, Object> attr = new HashMap<>(8);
        attr.put("userId", uid);
        attr.put("userName", params.get("username"));
        info.setExt(attr);

        String sessionId;
        AuthenticateSession authSession = RequestContextHolder.getContext().getAuthSession();

        if (null == authSession) {
            AuthenticateSession session = new AuthenticateSession();
            sessionId = UuidUtils.jdkUUID();
            session.setSessionId(sessionId);
            session.setCreateTime(System.currentTimeMillis());
            session.setUserId(uid);
            session.setUserAttributes(attr);
            session.setTtl(AuthConstant.SESSION_TTL);
            authSession = session;
            RequestContextHolder.getContext().setAuthSession(session);
        } else {
            sessionId = authSession.getSessionId();
            authSession.setUserAttributes(attr);
            authSession.setUserId(uid);
            authSession.setUpdateTime(System.currentTimeMillis());
        }

        sessionService.createSession(authSession);
        CookieUtils.setCookie(SessionServiceImpl.AUTH_SESSION_COOKIE_KEY, sessionId, AuthConstant.SESSION_TTL, response);

        return redirect;
    }

    @Override
    public Map<String, Object> userInfo(HttpServletRequest request, HttpServletResponse response) {
        String token = HttpRequestUtils.bearerTokenValue(request.getHeader(AuthConstant.AUTHORIZATION_KEY));
        log.info("token [{}]", token);
        AuthenticationToken aToken = tokenService.getOauthToken(token);
        if (StringUtils.isBlank(token) || null == aToken) {
            log.error("请求 token 为空");
            throw new AuthenticationException("请求 token 不可为空");
        }
        AuthenticateSession authSession = sessionService.getSessionById(aToken.getSessionId(), response);
        if (authSession == null) {
            log.error("用户未认证，认证 session 不存在");
            throw new AuthenticationException("用户未认证，认证 session 不存在");
        }
        return Optional.ofNullable(authSession.getUserAttributes()).orElse(Collections.emptyMap());
    }

}
