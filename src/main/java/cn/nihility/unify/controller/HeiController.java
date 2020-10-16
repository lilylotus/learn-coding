package cn.nihility.unify.controller;

import cn.nihility.unify.annotaion.UnifyResponse;
import cn.nihility.unify.exception.UnifyException;
import cn.nihility.unify.pojo.UnifyResult;
import cn.nihility.unify.pojo.UnifyResultCode;
import cn.nihility.unify.pojo.UnifyResultError;
import cn.nihility.unify.pojo.UnifyResultUtil;
import cn.nihility.unify.vo.TestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
