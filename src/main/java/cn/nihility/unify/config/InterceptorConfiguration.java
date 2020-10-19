package cn.nihility.unify.config;

import cn.nihility.unify.interceptor.LogTraceIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 实现拦截器 要拦截的路径以及不拦截的路径
        registry.addInterceptor(new LogTraceIdInterceptor()).addPathPatterns("/**");
    }

}
