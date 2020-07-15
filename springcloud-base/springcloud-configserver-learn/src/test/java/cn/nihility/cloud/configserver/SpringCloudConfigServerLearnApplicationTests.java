package cn.nihility.cloud.configserver;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.config.server.environment.EnvironmentController;

@SpringBootTest(properties = { "spring.profiles.active=native" })
class SpringCloudConfigServerLearnApplicationTests {

    @Autowired
    private EnvironmentController controller;

    @Test
    void contextLoads() {
        Assertions.assertThat(this.controller).isNotNull();
    }

}
