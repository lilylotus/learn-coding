package cn.nihility.boot;

import cn.nihility.boot.model.Foo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.BDDAssertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringBootThymeleafApplication.class},
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootThymeleafApplicationTests {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "http://localhost:";

    @Test
    public void testGetPlainJson() {
        final ResponseEntity<String> entity = restTemplate.getForEntity(baseUrl + port + "/rest/hei", String.class);
        then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(entity.getBody()).isEqualTo("hei");
    }

    @Test
    public void testGetPojo() {
        final Foo foo = restTemplate.getForObject(baseUrl + port + "/rest/foo", Foo.class);
        System.out.println(foo);
        assertThat(foo.getId()).isEqualTo(10000);
        then(foo.getName()).isEqualTo("default Name");
    }

    @Test
    public void testRequestWithHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("id", "1");
        headers.add("name", "headerName");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<String, String> params = new HashMap<>();
        params.put("id", "111");
        params.put("name", "paramName");

//        final ResponseEntity<Foo> exchange = restTemplate.exchange(baseUrl + port + "/rest/foo?name=headerName&id=11", HttpMethod.GET, entity, Foo.class);
        final Foo foo = restTemplate.getForObject(baseUrl + port + "/rest/foo", Foo.class, params);

        System.out.println(foo);

    }

    @Test
    public void testRequestWithParameters() {
        String url = baseUrl + port + "/rest/foo2/{id}/{name}";
        Map<String, String> params = new HashMap<>();
        params.put("id", "111");
        params.put("name", "paramName");
//        final Foo foo = restTemplate.getForObject(url, Foo.class, params);

        final Foo foo = restTemplate.getForObject(url, Foo.class, 111, "addName");

        System.out.println(foo);
    }

    @Test
    public void testGetHeader() {

        String uri = baseUrl + port + "/rest/foo-header";
        HttpHeaders headers = new HttpHeaders();
        headers.set("id", "1000");
        headers.add("name", "addHeader");
        HttpEntity<Foo> requestEntity = new HttpEntity<>(null, headers);
        final ResponseEntity<Foo> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Foo.class);
        final Foo body = responseEntity.getBody();
        System.out.println(body);
        then(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void testPost() {
        String uri = baseUrl + port + "/rest/foo2";
        Foo foo = new Foo(123, "POST-Name");
        final ResponseEntity<Foo> responseEntity = restTemplate.postForEntity(uri, foo, Foo.class);
        final Foo result = responseEntity.getBody();
        System.out.println(result);
        System.out.println(responseEntity.getStatusCodeValue());

    }

    @Test
    public void testPost2() {
        String uri = baseUrl + port + "/rest/foo21";
        Foo foo = new Foo(123, "POST-Name");
        HttpHeaders headers = new HttpHeaders();
        headers.set("h1", "h1 Header Value");
        headers.set("h2", "h2 Header Value");
        HttpEntity<Foo> request = new HttpEntity<>(foo, headers);
        final ResponseEntity<Foo> responseEntity = restTemplate.postForEntity(uri, request, Foo.class);
        final Foo result = responseEntity.getBody();
        System.out.println(result);
        System.out.println(responseEntity.getStatusCodeValue());

    }

}
