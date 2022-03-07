package cn.nihility.plugin.servlet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*", filterName = "RequestDurationFilter")
@Order(1)
public class RequestDurationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestDurationFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String uri = ((HttpServletRequest) request).getRequestURI();
        String method = ((HttpServletRequest) request).getMethod();
        logger.info("Start request [{}:{}]", method, uri);
        long startMillis = System.currentTimeMillis();

        chain.doFilter(request, response);

        long endMillis = System.currentTimeMillis();
        logger.info("Request [{}:{}] duration [{}]ms", method, uri, (endMillis - startMillis));
    }

}
