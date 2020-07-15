package cn.nihility.cloud.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
// 表示以 config server 启动，和别的 spring 应用通信
@EnableConfigServer
public class SpringCloudConfigServerLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigServerLearnApplication.class, args);
    }

}
