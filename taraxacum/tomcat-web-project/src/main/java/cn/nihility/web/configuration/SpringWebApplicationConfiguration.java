package cn.nihility.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * spring web 启动配置文件
 *
 * @author dandelion
 * @date 2020-06-25 17:24
 */
@Configuration
@ComponentScan(basePackages = {"cn.nihility.web.spring"})
public class SpringWebApplicationConfiguration /*implements WebMvcConfigurer*/ {

    /* 配置请求的视图解析 */
    /*@Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/", ".jsp");
    }*/

    /* Bean 配置和上面配置效果一样 */
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

}
