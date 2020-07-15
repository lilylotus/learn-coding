package cn.nihility.cloud.eureka;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
@EnableAdminServer
public class SpringCloudEurekaServerLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudEurekaServerLearnApplication.class, args);
    }

}
