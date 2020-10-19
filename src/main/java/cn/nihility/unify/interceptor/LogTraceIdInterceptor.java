package cn.nihility.unify.interceptor;

import cn.nihility.unify.constant.Constants;
import cn.nihility.unify.util.TraceIdUtil;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志拦截器，在所有请求日志中添加 Trace ID, 便于跟踪日志
 */
public class LogTraceIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果有上层使用就沿用上层传递的 Trace ID
        String traceId;
        MDC.put(Constants.TRACE_ID,
                ((traceId = request.getHeader(Constants.TRACE_ID)) == null ? TraceIdUtil.generateTraceId() : traceId));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 调用结束后删除 Trace ID
        MDC.remove(Constants.TRACE_ID);
    }
}
