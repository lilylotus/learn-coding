package cn.nihility.cloud.openfeign.config;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.netflix.hystrix.HystrixCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nihility
 * @date 2022/02/16 10:12
 */
@Configuration
public class HystrixCircuitBreakerFactoryConfiguration {

    @Bean
    public Customizer<HystrixCircuitBreakerFactory> defaultConfig() {
        return factory -> factory.configureDefault(id -> HystrixCommand.Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey(id))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(6000)));
    }

    /*@Bean
    public Customizer<HystrixCircuitBreakerFactory> customizer() {
        return factory -> factory.configure(builder -> builder.commandProperties(
            HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(2000)), "CloudEurekaClient");
    }*/

}
