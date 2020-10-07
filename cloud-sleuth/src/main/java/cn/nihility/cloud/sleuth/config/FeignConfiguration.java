package cn.nihility.cloud.sleuth.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public Logger.Level openFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
