package cn.nihility.profile.config;

import cn.nihility.profile.bean.PropertyBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PropertyBean.class)
public class PropertyConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "another")
    public PropertyBean anotherPropertyBean() {
        return new PropertyBean();
    }

}
