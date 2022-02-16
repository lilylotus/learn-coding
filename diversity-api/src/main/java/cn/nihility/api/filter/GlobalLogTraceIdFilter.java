package cn.nihility.api.filter;

import cn.nihility.common.util.UuidUtils;
import cn.nihility.plugin.log4j2.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*", filterName = "GlobalLogTraceIdFilter")
public class GlobalLogTraceIdFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(GlobalLogTraceIdFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String traceId;
        // slf4j
        MDC.put(Constant.TRACE_ID,
            ((traceId = ((HttpServletRequest) request).getHeader(Constant.TRACE_ID)) == null ?
                UuidUtils.snowflakeId() : traceId));
        // log4j2 -> ThreadContext.put(Constant.TRACE_ID, traceId);
        long startMillis = System.currentTimeMillis();
        String uri = ((HttpServletRequest) request).getRequestURI();
        String method = ((HttpServletRequest) request).getMethod();
        logger.info("start request [{}:{}]", method, uri);

        chain.doFilter(request, response);

        long endMillis = System.currentTimeMillis();
        logger.info("end request [{}:{}] cost [{}]ms", method, uri, (endMillis - startMillis));
        MDC.remove(Constant.TRACE_ID);
    }

}
