package cn.nihility.boot.config;

import cn.nihility.boot.controller.body.HttpServletRequestReplacedFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyFilterConfig {

    /**
     * 封装请求中的 body，可以多次获取
     */
    @Bean
    public FilterRegistrationBean<HttpServletRequestReplacedFilter> filterFilterRegistrationBean() {
        final FilterRegistrationBean<HttpServletRequestReplacedFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HttpServletRequestReplacedFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter("initParam", "HttpServletRequestReplacedFilter Init Param");
        registrationBean.setName("MyHttpServletRequestReplacedFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
