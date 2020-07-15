package cn.nihility.learn.config;

import cn.nihility.learn.bean.AutoImportBean;
import cn.nihility.learn.bean.AutoImportBeanConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AutoImportBeanConfiguration
 * 使用 spring boot starter 自动
 * @author dandelion
 * @date 2020-03-24 17:35
 */
@Configuration
@EnableConfigurationProperties(AutoImportBean.class)
public class AutoImportBeanConfiguration {

    @Autowired
    private AutoImportBean autoImportBean;

    @Bean
    @ConditionalOnMissingBean
    public AutoImportBeanConfig autoImportBeanConfig(AutoImportBean autoImportBean) {
        return new AutoImportBeanConfig(autoImportBean);
    }

}
