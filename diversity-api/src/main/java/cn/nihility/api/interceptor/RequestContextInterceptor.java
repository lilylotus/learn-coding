package cn.nihility.api.interceptor;

import cn.nihility.api.service.ISessionService;
import cn.nihility.common.entity.AuthenticateSession;
import cn.nihility.common.http.RequestContext;
import cn.nihility.common.http.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理请求上下文的拦截器
 *
 * @author nihility
 * @date 2022/02/17 17:31
 */
public class RequestContextInterceptor implements HandlerInterceptor {

    private final ISessionService sessionService;

    public RequestContextInterceptor(ISessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        RequestContext context = RequestContextHolder.getContext();
        RequestContextHolder.assembleContext(request, context);
        if (sessionService != null) {
            AuthenticateSession session = sessionService.getSessionFromCookie(request, response);
            context.setAuthSession(session);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        long endMillis = System.currentTimeMillis();
        RequestContext context = RequestContextHolder.getContext();
        context.setEnd(endMillis);
        context.setStatusCode(response.getStatus());

        RequestContextHolder.removeContext();

    }

}
