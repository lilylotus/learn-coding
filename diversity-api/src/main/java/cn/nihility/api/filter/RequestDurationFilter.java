package cn.nihility.api.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*", filterName = "RequestDurationFilter")
public class RequestDurationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestDurationFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long startMillis = System.currentTimeMillis();
        String uri = ((HttpServletRequest) request).getRequestURI();
        String method = ((HttpServletRequest) request).getMethod();
        logger.info("start request [{}:{}]", method, uri);

        chain.doFilter(request, response);

        long endMillis = System.currentTimeMillis();
        logger.info("end request [{}:{}] cost [{}]ms", method, uri, (endMillis - startMillis));
    }

}
