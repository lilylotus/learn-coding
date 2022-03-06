package cn.nihility.plugin.log4j2.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author sakura
 * @date 2022-03-06 16:59
 */
@Configuration
public class Log4j2Configuration {

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ServletComponentScan(basePackages = {"cn.nihility.plugin.log4j2.filter"})
    static class WebTraceIdFilterConfiguration {
    }

    /*@ConditionalOnClass(WebMvcConfigurer.class)
    public class InterceptorConfiguration implements WebMvcConfigurer {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            *//* 拦截所有 http 请求，添加日志 trace id 记录 *//*
            registry.addInterceptor(new Log4j2TraceIdInterceptor()).addPathPatterns("/**");
        }

    }*/

}
