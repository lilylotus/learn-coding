package cn.nihility.unify.controller;

import cn.nihility.unify.annotaion.UnifyResponse;
import cn.nihility.unify.exception.UnifyException;
import cn.nihility.unify.pojo.UnifyResult;
import cn.nihility.unify.pojo.UnifyResultCode;
import cn.nihility.unify.pojo.UnifyResultError;
import cn.nihility.unify.pojo.UnifyResultUtil;
import cn.nihility.unify.util.HttpClientUtil;
import cn.nihility.unify.util.RestTemplateUtil;
import cn.nihility.unify.vo.TestEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/hei")
public class HeiController {

    private static final Logger log = LoggerFactory.getLogger(HeiController.class);

    @RequestMapping("/success")
    public UnifyResult success() {
        return UnifyResultUtil.success();
    }

    @RequestMapping("/response_code")
    public UnifyResult responseCode() {
        return UnifyResultUtil.success(UnifyResultCode.ACCEPTED, "测试状态");
    }

    @RequestMapping("/map")
    public Map<String, Object> map() {
        Map<String, Object> result = new HashMap<>(4);
        result.put("content", "response content map");
        return result;
    }

    @RequestMapping("/unify")
    @UnifyResponse
    public Map<String, Object> mapWithUnifyResult() {
        Map<String, Object> result = new HashMap<>(4);
        result.put("content", "response unify content map");
        return result;
    }

    @RequestMapping("/fail")
    public UnifyResultError failure() {
        return UnifyResultUtil.failure(UnifyResultCode.PARAM_IS_INVALID, "测试失败接口");
    }

    @RequestMapping("/exception")
    public UnifyResultError exception() {
        throw new IllegalArgumentException("无效参数");
    }

    @RequestMapping("/exception1")
    public UnifyResultError exception1() {
        throw new UnifyException("自定义统一异常测试debug", UnifyResultCode.TEST_UNIFY_EXCEPTION);
    }

    @RequestMapping("/entity")
    public TestEntity entity() {
        TestEntity entity = new TestEntity();
        entity.setDate(new Date());
        entity.setInstant(Instant.now());
        entity.setLocalDate(LocalDate.now());
        entity.setLocalTime(LocalTime.now());
        entity.setLocalDateTime(LocalDateTime.now());
        return entity;
    }

    @PostMapping("/entity")
    public TestEntity entityStr(@RequestBody TestEntity entity) {
        log.info("Body Entity [{}]", entity);
        return entity;
    }

    @GetMapping("/web_util")
    public void webUtilRequest() {
        try {
            String response = RestTemplateUtil.doGet("http://127.0.0.1:49000/v1/hei/success", UnifyResult.class);
            log.info("Rest Template Util Response [{}]", response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String result = HttpClientUtil.doGet("http://127.0.0.1:49000/v1/hei/success");
        log.info("HttpClient DoGet Result [{}]", result);
    }

}
