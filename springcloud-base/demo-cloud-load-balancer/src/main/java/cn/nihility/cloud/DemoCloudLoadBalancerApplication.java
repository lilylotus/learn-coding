package cn.nihility.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DemoCloudLoadBalancerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoCloudLoadBalancerApplication.class, args);
    }

}
