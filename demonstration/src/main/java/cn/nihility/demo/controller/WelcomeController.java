package cn.nihility.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WelcomeController {

    private static final Logger log = LoggerFactory.getLogger(WelcomeController.class);

    @GetMapping("/welcome")
    public Map<String, Object> welcome() {

        log.info("Welcome Controller");

        Map<String, Object> result = new HashMap<>();

        result.put("message", "Welcome!");

        return result;

    }

}
