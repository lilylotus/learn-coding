package cn.nihility.api.controller;

import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class WelcomeController {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    @GetMapping("/welcome")
    public UnifyResult welcome() {
        logger.info("welcome");
        return UnifyResultUtil.success("Welcome");
    }

    @GetMapping("/welcome/exception")
    public UnifyResult welcomeException() {
        logger.info("welcome exception");

        Random random = new Random(System.currentTimeMillis());
        int v = random.nextInt(10);
        if (v % 3 == 0) {
            throw new IllegalArgumentException("Welcome IllegalArgumentException");
        } else if (v % 3 == 1) {
            throw new NullPointerException("Welcome NullPointerException");
        }

        return UnifyResultUtil.failure("Welcome welcomeException");
    }

    @GetMapping("/welcome/http")
    public UnifyResult welcomeHttpRequestException() {
        logger.info("welcome HttpRequestException");
        throw new HttpRequestException(HttpStatus.BAD_GATEWAY, UnifyResultUtil.failure("welcome HttpRequestException"));
    }

}