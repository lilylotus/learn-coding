package cn.nihility.cloud.openfeign.config;

import feign.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    @ConditionalOnMissingBean(Logger.Level.class)
    public Logger.Level openfeignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
