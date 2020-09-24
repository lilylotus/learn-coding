package cn.nihility.cloud.ribbon.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RibbonConfiguration {

    /**
     * 加了 @LoadBalanced 注解后，该 RestTemplate 就具备了负载功能
     * 可实现自动负载，不用 LoadBalancerClient 工具手动配置
     * Ribbon 使用 RestTemplate 对服务的消费访问
     * Ribbon 提供负载均衡
     *
     * 没有 ribbon 使用 cloud loadbalancer (需要手动配置)
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
