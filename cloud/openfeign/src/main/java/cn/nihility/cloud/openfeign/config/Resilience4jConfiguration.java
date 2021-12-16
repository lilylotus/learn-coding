package cn.nihility.cloud.openfeign.config;

import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4jBulkheadConfigurationBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4jBulkheadProvider;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * FixedThreadPoolBulkhead uses a bounded queue and a fixed thread pool
 */
@Configuration
public class Resilience4jConfiguration {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultResilience4JCircuitBreakerCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2)).build())
            .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
            .build());
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> slowResilience4JCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder -> builder.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2)).build()),
            "slowRandomTimeOut");
    }

    @Bean
    public Customizer<Resilience4jBulkheadProvider> resilience4jSpecificConfigBulkheadCustomizer() {
        return provider -> provider.configure(id -> new Resilience4jBulkheadConfigurationBuilder()
                .bulkheadConfig(BulkheadConfig.custom()
                    .maxConcurrentCalls(10).maxWaitDuration(Duration.ofMillis(100L)).build())
                .build(),
            "SpecificConfigBulkhead");
    }

    @Bean
    public Customizer<Resilience4jBulkheadProvider> resilience4jSpecificConfigThreadPoolBulkheadCustomizer() {
        return provider -> provider.configure(builder -> builder
                .bulkheadConfig(BulkheadConfig.custom().maxConcurrentCalls(10).build())
                .threadPoolBulkheadConfig(ThreadPoolBulkheadConfig.custom()
                    .coreThreadPoolSize(8).maxThreadPoolSize(8).queueCapacity(2).build()),
            "SpecificConfigThreadPoolBulkhead");
    }

}
