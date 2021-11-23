package cn.nihility.plugins.log4j2.interceptor;

import cn.nihility.plugins.log4j2.constant.Constant;
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
        final String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String traceId;
        MDC.put(Constant.TRACE_ID,
            ((traceId = request.getHeader(Constant.TRACE_ID)) == null ? uuid : traceId));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 调用结束后删除 Trace ID
        MDC.remove(Constant.TRACE_ID);
    }

}
