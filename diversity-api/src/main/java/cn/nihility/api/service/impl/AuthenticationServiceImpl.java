package cn.nihility.api.service.impl;

import cn.nihility.api.service.IAuthenticationService;
import cn.nihility.api.service.ISessionService;
import cn.nihility.api.util.CookieUtils;
import cn.nihility.common.constant.AuthConstant;
import cn.nihility.common.entity.AuthenticateSession;
import cn.nihility.common.entity.UserInfo;
import cn.nihility.common.http.RequestContextHolder;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.common.util.UuidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/21 10:26
 */
@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private ISessionService sessionService;

    public AuthenticationServiceImpl(ISessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public String auth(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> params = HttpRequestUtils.paramsToMap(request);
        String redirect = params.get(AuthConstant.REDIRECT_TAG);
        log.info("redirect url [{}]", redirect);

        UserInfo info = new UserInfo();
        String uid = UuidUtils.jdkUUID();
        info.setUserId(uid);
        info.setUserName("Test User Name");

        Map<String, Object> attr = new HashMap<>(8);
        attr.put("userId", uid);
        attr.put("userName", "Test User Name");
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
        CookieUtils.setCookie(SessionServiceImpl.AUTH_SESSION_COOKIE_KEY, sessionId, 3600L, response);

        return redirect;
    }

}
