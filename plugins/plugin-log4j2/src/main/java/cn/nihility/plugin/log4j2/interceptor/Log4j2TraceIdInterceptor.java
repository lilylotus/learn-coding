package cn.nihility.plugin.log4j2.interceptor;

import cn.nihility.plugin.log4j2.constant.Constant;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 日志拦截器，在所有请求日志中添加 Trace ID, 便于跟踪日志
 */
public class Log4j2TraceIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果有上层使用就沿用上层传递的 Trace ID
        String traceId = request.getHeader(Constant.TRACE_ID);
        MDC.put(Constant.TRACE_ID, (traceId == null ? UUID.randomUUID().toString().replace("-", "") : traceId));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 调用结束后删除 Trace ID
        MDC.remove(Constant.TRACE_ID);
    }

}
