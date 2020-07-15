package cn.nihility.cloud.eureka.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * EurekaClientController
 *
 * @author dandelion
 * @date 2020-04-10 14:41
 */
@RestController
public class EurekaClientController {

    @GetMapping("/hello")
    public String hello()  {
        return "Hello Eureka Client";
    }

}
