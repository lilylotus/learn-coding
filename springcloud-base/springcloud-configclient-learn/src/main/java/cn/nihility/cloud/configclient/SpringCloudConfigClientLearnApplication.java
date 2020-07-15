package cn.nihility.cloud.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringCloudConfigClientLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigClientLearnApplication.class, args);
    }

}

@RefreshScope
@RestController
class MessageRestController {
    @Value("${message:Hello Default}")
    private String message;

    @Value("${local.message:default local message}")
    private String localMessage;

    @RequestMapping("/message")
    public String getMessage() {
        return this.message + " : " + this.localMessage;
    }
}
