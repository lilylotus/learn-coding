package cn.nihility.api.interceptor;

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestContextHolder.assembleContext(request);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        long endMillis = System.currentTimeMillis();
        RequestContext context = RequestContextHolder.getContext();
        context.setEnd(endMillis);
        context.setStatusCode(response.getStatus());

        RequestContextHolder.removeContext();

    }

}
