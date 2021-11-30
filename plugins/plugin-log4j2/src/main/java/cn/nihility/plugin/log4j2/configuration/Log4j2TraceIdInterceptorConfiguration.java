package cn.nihility.plugin.log4j2.configuration;

import cn.nihility.plugin.log4j2.interceptor.Log4j2TraceIdInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ConditionalOnClass(WebMvcConfigurer.class)
public class Log4j2TraceIdInterceptorConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /* 拦截所有 http 请求，添加日志 trace id 记录 */
        registry.addInterceptor(new Log4j2TraceIdInterceptor()).addPathPatterns("/**");
    }

}
