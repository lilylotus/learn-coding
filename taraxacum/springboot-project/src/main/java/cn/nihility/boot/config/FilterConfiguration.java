package cn.nihility.boot.config;

import cn.nihility.boot.filter.LogFilter;
import cn.nihility.boot.util.LogLevel;
import cn.nihility.boot.util.LoggerUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Filter 过滤器的配置类, 同时可以通过注解的方式配置 Filter
 * @author dandelion
 * @date 2020:06:27 10:39
 */
@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean<LogFilter> filterRegistrationBean() {
        LoggerUtil.log(getClass(), LogLevel.INFO, "Registry Filter LogFilter");

        FilterRegistrationBean<LogFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LogFilter());
        bean.addUrlPatterns("/*");
        bean.setName("LogDurationFilter");
        bean.setOrder(1);

        return bean;
    }

}
