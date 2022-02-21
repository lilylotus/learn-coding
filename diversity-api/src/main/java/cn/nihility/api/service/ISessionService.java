package cn.nihility.api.service;

import cn.nihility.common.entity.AuthenticateSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author nihility
 * @date 2022/02/18 13:59
 */
public interface ISessionService {

    /**
     * 从 cookie 中获取认证 session
     */
    AuthenticateSession getSessionFromCookie(HttpServletRequest request, HttpServletResponse response);

    AuthenticateSession getSessionById(String id, HttpServletResponse response);

    void createSession(AuthenticateSession session);

    void updateSession(AuthenticateSession session);

}
