package cn.nihilty.gateway.refresh;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(GatewayAutoConfiguration.class)
public class DynamicGatewayRefreshConfiguration {

    @Bean
    public DynamicGatewayRefresh dynamicGatewayRefresh(GatewayProperties gatewayProperties) {
        return new DynamicGatewayRefresh(gatewayProperties);
    }

}
