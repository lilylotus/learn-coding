package cn.nihility.cloud.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CloudEurekaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudEurekaClientApplication.class, args);
    }

}
