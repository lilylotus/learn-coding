package cn.nihility.api.config;

import cn.nihility.api.interceptor.ApiAuthenticationInterceptor;
import cn.nihility.api.interceptor.IdempotentInterceptor;
import cn.nihility.api.interceptor.RequestContextInterceptor;
import cn.nihility.api.service.IdempotentService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 *
 * @author nihility
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    private IdempotentService idempotentService;

    public InterceptorConfiguration(IdempotentService idempotentService) {
        this.idempotentService = idempotentService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /* 请求上下文注入 */
        registry.addInterceptor(new RequestContextInterceptor()).addPathPatterns("/**");
        /* 拦截幂等性校验 token key */
        registry.addInterceptor(new IdempotentInterceptor(idempotentService)).addPathPatterns("/**");
        /* 身份认证校验 */
        registry.addInterceptor(new ApiAuthenticationInterceptor()).addPathPatterns("/**");
    }

}
