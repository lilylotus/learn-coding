package cn.nihility.boot.controller;

import cn.nihility.boot.controller.ret.ResultResponse;
import cn.nihility.boot.redis.impl.KeyValueCacheServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author dandelion
 * @date 2020-09-19 22:57
 */
@RestController
@RequestMapping(value = "/redis", produces = MediaType.APPLICATION_JSON_VALUE)
public class RedisController {

    private static final Logger log = LoggerFactory.getLogger(RedisController.class);

    private final StringRedisTemplate stringRedisTemplate;
    private final KeyValueCacheServiceImpl  keyValueCacheService;

    public RedisController(StringRedisTemplate stringRedisTemplate, KeyValueCacheServiceImpl keyValueCacheService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.keyValueCacheService = keyValueCacheService;
    }

    @GetMapping("/put/{key}/{value}")
    public ResultResponse<Object> setKeyValue(@PathVariable String key, @PathVariable String value) {
        log.info("redis key set [{}:{}]", key, value);
        return ResultResponse.success(keyValueCacheService.set(key, value));
    }

    @RequestMapping(value = "/get/{key}", method = RequestMethod.GET)
    public ResultResponse<Object> queryKey(@PathVariable String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        log.info("redis key get [{}:{}]", key, value);
        return ResultResponse.success(value);
    }
}
