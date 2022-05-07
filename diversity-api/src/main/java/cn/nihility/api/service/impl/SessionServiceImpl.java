package cn.nihility.api.service.impl;

import cn.nihility.api.service.ISessionService;
import cn.nihility.api.util.CookieUtils;
import cn.nihility.common.entity.AuthenticateSession;
import cn.nihility.common.http.RequestContext;
import cn.nihility.common.http.RequestContextHolder;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.plugin.redis.service.RedissonOperateService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author nihility
 * @date 2022/02/18 14:09
 */
@Service
public class SessionServiceImpl implements ISessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionServiceImpl.class);

    public static final String AUTH_SESSION_COOKIE_KEY = "authentication_session";

    private final RedissonOperateService redissonOp;

    public SessionServiceImpl(RedissonOperateService redissonOp) {
        this.redissonOp = redissonOp;
    }

    private String authenticationSessionKey(String sessionId) {
        return AUTH_SESSION_COOKIE_KEY + ":" + sessionId;
    }

    @Override
    public AuthenticateSession getSessionFromCookie(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = HttpRequestUtils.obtainCookieValue(AUTH_SESSION_COOKIE_KEY, request);
        if (StringUtils.isNotBlank(sessionId)) {
            RBucket<AuthenticateSession> bucket = redissonOp.getBucket(authenticationSessionKey(sessionId));
            AuthenticateSession session = bucket.get();
            if (session == null) {
                log.warn("请求话实例不存在，删除对应 cookies");
                CookieUtils.delCookie(AUTH_SESSION_COOKIE_KEY, response);
            } else {
                RequestContext context = RequestContextHolder.getContext();
                context.setLoginBefore(true);
                context.setAuthSession(session);
                return session;
            }
        }
        return null;
    }

    @Override
    public AuthenticateSession getSessionById(String sessionId, HttpServletResponse response) {
        if (StringUtils.isNotBlank(sessionId)) {
            RBucket<AuthenticateSession> bucket = redissonOp.getBucket(authenticationSessionKey(sessionId));
            AuthenticateSession session = bucket.get();
            if (session == null) {
                log.warn("认证会话实例不存在，删除对应 cookies");
                CookieUtils.delCookie(AUTH_SESSION_COOKIE_KEY, response);
            } else {
                RequestContextHolder.getContext().setLoginBefore(true);
                return session;
            }
        }
        return null;
    }

    @Override
    public void createSession(AuthenticateSession session) {
        RBucket<AuthenticateSession> bucket = redissonOp.getBucket(authenticationSessionKey(session.getSessionId()));
        bucket.set(session, session.getTtl(), TimeUnit.SECONDS);
    }

    @Override
    public void updateSession(AuthenticateSession session) {
        RBucket<AuthenticateSession> bucket = redissonOp.getBucket(authenticationSessionKey(session.getSessionId()));
        bucket.setIfExists(session, session.getTtl(), TimeUnit.SECONDS);
    }

}
