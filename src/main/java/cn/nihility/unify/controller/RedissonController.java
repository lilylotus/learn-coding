package cn.nihility.unify.controller;

import cn.nihility.unify.distribute.DistributedLock;
import cn.nihility.unify.distribute.SimpleDistributedLock;
import cn.nihility.unify.exception.UnifyException;
import cn.nihility.unify.pojo.UnifyResult;
import cn.nihility.unify.pojo.UnifyResultCode;
import cn.nihility.unify.pojo.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/v1/redisson")
public class RedissonController {

    private static final Logger log = LoggerFactory.getLogger(RedissonController.class);
    private final DistributedLock distributedLock;
    private final SimpleDistributedLock simpleDistributedLock;

    public RedissonController(DistributedLock distributedLock, SimpleDistributedLock simpleDistributedLock) {
        this.distributedLock = distributedLock;
        this.simpleDistributedLock = simpleDistributedLock;
    }

    @RequestMapping("/lock/{key}")
    public UnifyResult lock(@PathVariable String key) {
        log.info("lock key [{}]", key);
        if (StringUtils.isEmpty(key)) {
            throw new UnifyException("key cannot be empty", UnifyResultCode.PARAM_IS_BLANK);
        }
        distributedLock.lock(key);
        return UnifyResultUtil.success();
    }

    @RequestMapping("/lock1/{key}")
    public UnifyResult lock1(@PathVariable String key) {
        log.info("lock1 key [{}]", key);
        if (StringUtils.isEmpty(key)) {
            throw new UnifyException("key cannot be empty", UnifyResultCode.PARAM_IS_BLANK);
        }
        return UnifyResultUtil.success(distributedLock.lock(key, 1000, 3000,
                () -> "Lock Success", () -> "Lock Failure"));
    }


    @RequestMapping("/lock2/{key}")
    public UnifyResult lock2(@PathVariable String key) {
        log.info("lock2 key [{}]", key);
        if (StringUtils.isEmpty(key)) {
            throw new UnifyException("key cannot be empty", UnifyResultCode.PARAM_IS_BLANK);
        }

        simpleDistributedLock.lock(key, 10, TimeUnit.MINUTES);
        log.info("time [{}] , key [{}], begin", LocalDateTime.now(), key);
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(1000);
                log.info("time [{}] , key [{}], index [{}]", LocalDateTime.now(), key, i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("time [{}] , key [{}], end", LocalDateTime.now(), key);

        simpleDistributedLock.unlock(key);

        return UnifyResultUtil.success();
    }

    @RequestMapping("/unlock/{key}")
    public UnifyResult unlock(@PathVariable String key) {
        log.info("unlock key [{}]", key);
        if (StringUtils.isEmpty(key)) {
            throw new UnifyException("key cannot be empty", UnifyResultCode.PARAM_IS_BLANK);
        }
        distributedLock.releaseLock(key);
        return UnifyResultUtil.success();
    }

}
