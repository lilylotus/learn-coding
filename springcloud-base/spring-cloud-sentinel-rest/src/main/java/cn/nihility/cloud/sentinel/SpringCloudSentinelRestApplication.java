package cn.nihility.cloud.sentinel;

import cn.nihility.cloud.sentinel.exception.ExceptionUtils;
import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
public class SpringCloudSentinelRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudSentinelRestApplication.class, args);
    }

    @Bean
    @LoadBalanced
    @SentinelRestTemplate(fallbackClass = ExceptionUtils.class, fallback = "handleFallBack",
            blockHandlerClass = ExceptionUtils.class, blockHandler = "handleBlockHandler")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
