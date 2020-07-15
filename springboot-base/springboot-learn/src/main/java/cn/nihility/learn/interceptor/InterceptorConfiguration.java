package cn.nihility.learn.interceptor;

import cn.nihility.learn.annotation.AutoIdempotent;
import cn.nihility.learn.idempotent.TokenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * InterceptorConfiguration
 *
 * @author dandelion
 * @date 2020-05-07 17:01
 */
@Slf4j
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Autowired
    private TokenServiceImpl tokenService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("registration idempotent interceptor.");
        final InterceptorRegistration registration = registry.addInterceptor(new AutoIdempotentInterceptor(tokenService));
        registration.addPathPatterns("/**");
        registration.excludePathPatterns("/error");
        registration.excludePathPatterns("/static/**");
        registration.excludePathPatterns("/login");
    }

}
