package cn.nihility.api.service.impl;

import cn.nihility.api.exception.IdempotentException;
import cn.nihility.api.service.IdempotentService;
import cn.nihility.common.util.SnowflakeIdWorker;
import cn.nihility.plugin.redis.service.RedisOperateService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 幂等性业务处理实现类
 */
@Service
public class IdempotentServiceImpl implements IdempotentService {

    private static final Logger log = LoggerFactory.getLogger(IdempotentServiceImpl.class);

    private static final String IDEMPOTENT_REDIS_KEY = "idempotent:";

    private RedisOperateService redisOperateService;

    public IdempotentServiceImpl(RedisOperateService redisOperateService) {
        this.redisOperateService = redisOperateService;
    }

    @Override
    public String generateToken() {
        final String token = SnowflakeIdWorker.nextLongStringId();
        if (log.isDebugEnabled()) {
            log.debug("生成幂等 token [{}]", token);
        }
        redisOperateService.set(IDEMPOTENT_REDIS_KEY + token, token, 1, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public boolean verify(String token) throws IdempotentException {
        if (StringUtils.isBlank(token)) {
            throw new IdempotentException("幂等校验 token 不可为空");
        }

        /* 校验该 token 是否存在，可能被消费掉了或过期了 */
        if (token.equals(redisOperateService.get(IDEMPOTENT_REDIS_KEY + token)) &&
            redisOperateService.deleteKey(IDEMPOTENT_REDIS_KEY + token)) {
            return true;
        }

        log.info("幂等失效 token [{}]", token);
        throw new IdempotentException("幂等 token 已失效");

    }

}
