package cn.nihility.unify.config;

import cn.nihility.unify.interceptor.AuthenticationInterceptor;
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
        /* 拦截所有 http 请求，添加日志 trace id 记录 */
        registry.addInterceptor(new LogTraceIdInterceptor()).addPathPatterns("/**");

        /* 拦截所有身份校验 */
        registry.addInterceptor(new AuthenticationInterceptor()).addPathPatterns("/**");

    }

}
