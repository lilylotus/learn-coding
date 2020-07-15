package cn.nihility.cloud;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DemoCloudLoadBalancerClientApplication.class},
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoCloudLoadBalancerClientApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void shouldReturn200WhenSendingRequestToRoot() {
        System.out.println("port " + port);
        final ResponseEntity<String> forEntity =
                testRestTemplate.getForEntity("http://localhost:" + port + "/", String.class);

        BDDAssertions.then(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(forEntity.getBody()).isEqualTo("Hi!");
    }

    @Test
    public void shouldReturn200WhenSendingRequestToGreeting() {
        System.out.println("port " + port);
        final ResponseEntity<String> entity =
                testRestTemplate.getForEntity("http://localhost:" + port + "/greeting", String.class);

        BDDAssertions.then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(entity.getBody()).isNotEmpty();
    }

}
