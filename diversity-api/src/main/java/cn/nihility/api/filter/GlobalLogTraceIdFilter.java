package cn.nihility.api.filter;

import cn.nihility.common.util.UuidUtil;
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
        MDC.put(Constant.TRACE_ID,
            ((traceId = ((HttpServletRequest) request).getHeader(Constant.TRACE_ID)) == null ?
                UuidUtil.snowflakeId() : traceId));
        if (logger.isDebugEnabled()) {
            logger.debug("request uri [{}]", ((HttpServletRequest) request).getRequestURI());
        }
        chain.doFilter(request, response);
        MDC.remove(Constant.TRACE_ID);
    }

}
