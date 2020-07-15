package cn.nihility.cloud;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DemoCloudLoadBalancerApplication.class,
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoCloudLoadBalancerApplicationTests {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;

    private ConfigurableApplicationContext applicationContext1;
    private ConfigurableApplicationContext applicationContext2;
    private ConfigurableApplicationContext applicationContext3;

    @BeforeEach
    public void startApps() {
        applicationContext1 = startApp(51001);
        applicationContext2 = startApp(51002);
        applicationContext3 = startApp(51003);
    }

    private ConfigurableApplicationContext startApp(int port) {
        return SpringApplication.run(TestApplication.class,
                "--server.port=" + port,
                "--spring.jmx.enabled=false");
    }

    @Configuration
    @EnableAutoConfiguration
    @RestController
    static class TestApplication {

        static AtomicInteger atomicInteger = new AtomicInteger();

        @RequestMapping(value = "/greeting")
        public Integer greet() {
            return atomicInteger.incrementAndGet();
        }

        @RequestMapping(value = "/")
        public String health() {
            return "ok";
        }
    }

    @AfterEach
    public void closeApps() {
        applicationContext1.close();
        applicationContext2.close();
        applicationContext3.close();
    }

   @Test
    public void shouldRoundRobbinOverInstanceWhenCallingServicesViaLoadBalancer() {
       final ResponseEntity<String> entity1 = testRestTemplate.getForEntity("http://localhost:" + port + "/hi", String.class);
       final ResponseEntity<String> entity2 = testRestTemplate.getForEntity("http://localhost:" + port + "/hi", String.class);
       final ResponseEntity<String> entity3 = testRestTemplate.getForEntity("http://localhost:" + port + "/hello", String.class);

       BDDAssertions.then(entity1.getStatusCode()).isEqualTo(HttpStatus.OK);
       BDDAssertions.then(entity1.getBody()).isEqualTo("1, Mary!");

       BDDAssertions.then(entity2.getStatusCode()).isEqualTo(HttpStatus.OK);
       BDDAssertions.then(entity2.getBody()).isEqualTo("2, Mary!");

       BDDAssertions.then(entity3.getStatusCode()).isEqualTo(HttpStatus.OK);
       BDDAssertions.then(entity3.getBody()).isEqualTo("3, John!");

   }

}
