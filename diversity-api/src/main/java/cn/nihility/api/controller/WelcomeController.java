package cn.nihility.api.controller;

import cn.nihility.api.annotation.UnifyResponseResult;
import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.api.pojo.DateContainer;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class WelcomeController {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    @GetMapping("/welcome")
    public UnifyResult<String> welcome() {
        logger.info("welcome");
        return UnifyResultUtils.success("Welcome");
    }

    @GetMapping("/welcome/string")
    public String welcomeString() {
        logger.info("welcome string");
        return "Welcome String，欢迎来到 String 返回。";
    }

    @GetMapping("/welcome/response")
    @UnifyResponseResult
    public Integer welcomeUnifyResponseResult() {
        logger.info("welcome UnifyResponseResult");
        return 520;
    }

    @GetMapping("/welcome/exception")
    public UnifyResult<String> welcomeException() {
        logger.info("welcome exception");

        Random random = new Random(System.currentTimeMillis());
        int v = random.nextInt(10);
        if (v % 3 == 0) {
            throw new IllegalArgumentException("Welcome IllegalArgumentException");
        } else if (v % 3 == 1) {
            throw new NullPointerException("Welcome NullPointerException");
        }

        return UnifyResultUtils.success("Welcome welcomeException");
    }

    @GetMapping("/welcome/http")
    public UnifyResult<String> welcomeHttpRequestException() {
        logger.info("welcome HttpRequestException");
        throw new HttpRequestException(HttpStatus.BAD_GATEWAY, UnifyResultUtils.failure("welcome HttpRequestException"));
    }

    @PostMapping("/welcome/date")
    public UnifyResult<DateContainer> welcomeDate(@RequestBody DateContainer dateContainer) {
        logger.info("date container [{}]", dateContainer);
        return UnifyResultUtils.success(dateContainer);
    }

    @GetMapping("/welcome/date")
    public UnifyResult<DateContainer> welcomeDateGet(DateContainer dateContainer) {
        logger.info("Get date container [{}]", dateContainer);
        return UnifyResultUtils.success(dateContainer);
    }

}
