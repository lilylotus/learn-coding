package cn.nihility.use.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplateController
 *
 * @author dandelion
 * @date 2020-04-12 11:07
 */
@RestController
@RequestMapping("/rest")
public class RestTemplateController {

    private static String baseUrl = "http://localhost:8080/rest/";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/hei", method = RequestMethod.GET)
    public String hei() {
        final ResponseEntity<String> entity = restTemplate.getForEntity(baseUrl + "hei", String.class);
        return entity.getBody() + ":" + entity.getStatusCode();
    }

}
