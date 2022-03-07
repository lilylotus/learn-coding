package cn.nihility.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    @GetMapping("//welcome/exception")
    public Map<String, Object> exception() {

        log.info("Welcome exception Controller");

        Map<String, Object> result = new HashMap<>();

        Random random = new Random(System.currentTimeMillis());
        if (random.nextInt(100) % 5 == 0) {
            throw new IllegalArgumentException("无效参数");
        }

        result.put("message", "Welcome exception!");

        return result;

    }

}
