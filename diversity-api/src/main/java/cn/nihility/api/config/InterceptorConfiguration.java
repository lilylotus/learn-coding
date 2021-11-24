package cn.nihility.api.config;

import cn.nihility.api.interceptor.IdempotentInterceptor;
import cn.nihility.api.service.IdempotentService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    private IdempotentService idempotentService;

    public InterceptorConfiguration(IdempotentService idempotentService) {
        this.idempotentService = idempotentService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /* 拦截幂等性校验 token key */
        registry.addInterceptor(new IdempotentInterceptor(idempotentService))
            .addPathPatterns("/**").excludePathPatterns("/**/idempotent/token");

    }

}
