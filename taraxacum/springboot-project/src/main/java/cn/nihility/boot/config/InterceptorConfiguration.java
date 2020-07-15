package cn.nihility.boot.config;

import cn.nihility.boot.interceptor.AuthenticationInterceptor;
import cn.nihility.boot.interceptor.AutoIdempotentInterceptor;
import cn.nihility.boot.service.TokenServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author dandelion
 * @date 2020:06:27 10:43
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final static Logger log = LoggerFactory.getLogger(InterceptorConfiguration.class);

    private final TokenServiceImpl tokenService;

    /* field 注入不推荐 */
    public InterceptorConfiguration(TokenServiceImpl tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Registry Authentication Interceptor");
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**");
        // 拦截所有的请求， 判断时候有 @LoginRequired 注解，决定是否需要登录

        /* 拦截幂等性校验 */
        InterceptorRegistration registration = registry.addInterceptor(new AutoIdempotentInterceptor(tokenService));
        registration.addPathPatterns("/**");
        registration.excludePathPatterns("/error");
        registration.excludePathPatterns("/static/**");
        registration.excludePathPatterns("/login");

    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}
