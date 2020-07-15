package cn.nihility.cloud.consul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudConsulServiceProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConsulServiceProviderApplication.class, args);
    }

}
