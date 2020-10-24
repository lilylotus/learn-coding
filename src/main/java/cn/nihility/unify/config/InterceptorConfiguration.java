package cn.nihility.unify.config;

import cn.nihility.unify.idempotent.IdempotentService;
import cn.nihility.unify.interceptor.AuthenticationInterceptor;
import cn.nihility.unify.interceptor.IdempotentInterceptor;
import cn.nihility.unify.interceptor.LogTraceIdInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final IdempotentService idempotentService;

    public InterceptorConfiguration(IdempotentService idempotentService) {
        this.idempotentService = idempotentService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 实现拦截器 要拦截的路径以及不拦截的路径
        /* 拦截所有 http 请求，添加日志 trace id 记录 */
        registry.addInterceptor(new LogTraceIdInterceptor()).addPathPatterns("/**");

        /* 拦截所有身份校验 */
        registry.addInterceptor(new AuthenticationInterceptor())
                .addPathPatterns("/**").excludePathPatterns("/**/login");

        /* 拦截幂等性校验 token key */
        registry.addInterceptor(new IdempotentInterceptor(idempotentService))
                .addPathPatterns("/**").excludePathPatterns("/**/idempotent/token");

    }

    /**
     * 跨域问题
     */
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}
