package cn.nihility.boot.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * 过滤器, 配置方式
 * 1. 通过 Servlet 注解, Servlet 3.0 及其以上支持
 * 2. 通过 @Configuration 配置 FilterRegistrationBean 注册 Filter
 *
 * @author dandelion
 * @date 2020:06:27 10:34
 */
//@WebFilter(urlPatterns = {"/*"}, filterName = "logDurationFilter")
public class LogFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LogFilter -> init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        log.info("LogFilter -> doFilter duration time [{}]", (System.currentTimeMillis() - start));
    }

    @Override
    public void destroy() {
        log.info("LogFilter -> destroy");
    }

}
