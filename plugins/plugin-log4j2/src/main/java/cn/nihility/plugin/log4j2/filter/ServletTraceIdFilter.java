package cn.nihility.plugin.log4j2.filter;

import cn.nihility.plugin.log4j2.constant.Constant;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@WebFilter(urlPatterns = "/*", filterName = "ServletTraceIdFilter")
public class ServletTraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        String traceId = ((HttpServletRequest) request).getHeader(Constant.TRACE_ID);

        // slf4j
        MDC.put(Constant.TRACE_ID, (traceId == null ? UUID.randomUUID().toString().replace("-", "") : traceId));

        chain.doFilter(request, response);

        MDC.remove(Constant.TRACE_ID);

    }

}
