package cn.nihility.cloud.configclient;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootTest
class SpringCloudConfigClientLearnApplicationTests {

    @Autowired
    private ConfigurableEnvironment environment;
    @Autowired
    private MessageRestController controller;
    @Autowired
    private ContextRefresher refresher;

    @Test
    void contextLoads() {

        String defaultString = "Hello Default : default local message";
        String localMessage = "spring cloud config client local application context message value.";

        /* Developer Message Value : spring cloud config client local application context message value. */
        Assertions.assertThat(this.controller.getMessage())
                .isNotEqualTo(defaultString);

        TestPropertyValues
                .of("message:Hello test")
                .applyTo(environment);

        String message = this.controller.getMessage();
        System.out.println(message);
        Assertions.assertThat(this.controller.getMessage())
                .isNotEqualTo(defaultString);

        refresher.refresh();

        message = this.controller.getMessage();
        System.out.println(message);
        Assertions.assertThat(this.controller.getMessage())
                .isEqualTo("Hello test : " + localMessage);


    }

}
