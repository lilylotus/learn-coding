package cn.nihility.plugin.servlet.filter;

import cn.nihility.common.constant.Constant;
import cn.nihility.common.util.UuidUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*", filterName = "RequestTraceIdFilter")
@Order(0)
public class RequestTraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        String traceId = ((HttpServletRequest) request).getHeader(Constant.TRACE_ID);

        MDC.put(Constant.TRACE_ID, (traceId == null ? UuidUtils.jdkUuid() : traceId));

        chain.doFilter(request, response);

        MDC.remove(Constant.TRACE_ID);

    }

}
