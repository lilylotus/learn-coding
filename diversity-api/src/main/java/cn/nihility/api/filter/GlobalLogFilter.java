package cn.nihility.api.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*", filterName = "GlobalLogFilter")
public class GlobalLogFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(GlobalLogFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("request uri [{}]", ((HttpServletRequest) request).getRequestURI());
        }
        chain.doFilter(request, response);
    }

}
