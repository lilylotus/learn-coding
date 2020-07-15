package cn.nihility.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * WenApplicationConfig
 * 类似于 spring boot Application 当中的配置扫描
 * @author dandelion
 * @date 2020-03-24 22:02
 */
@Configuration
@ComponentScan(value = {"cn.nihility.app"})
public class WebApplicationConfig /*implements WebMvcConfigurer*/ {

   /* @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/", ".jsp");
    }*/

    // 两种方式其实是一样的
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver viewResolver =
                new InternalResourceViewResolver();
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

}
