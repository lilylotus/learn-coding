package cn.nihility.api.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CORS 跨域过滤器配置，Spring Framework 5.x 版本推荐
 */
@Configuration
@ServletComponentScan
@WebFilter(filterName = "CorsFilter ", urlPatterns = "/*")
public class CorsFilterConfiguration implements Filter {

    /*
      Access-Control-Allow-Headers：
      注意以下这些特定的首部是一直允许的：Accept, Accept-Language, Content-Language, Content-Type
      （但只在其值属于 MIME 类型 application/x-www-form-urlencoded, multipart/form-data 或 text/plain中的一种时）
      这些被称作simple headers，无需特意声明它们
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers",
            "origin, x-requested-with, Origin, X-Requested-With, Authorization, authorization");
        chain.doFilter(request, response);
    }

}
