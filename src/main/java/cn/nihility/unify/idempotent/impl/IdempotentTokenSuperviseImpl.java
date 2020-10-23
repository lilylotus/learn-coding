package cn.nihility.unify.idempotent.impl;

import cn.nihility.unify.exception.IdempotentException;
import cn.nihility.unify.idempotent.IdempotentTokenSupervise;
import cn.nihility.unify.pojo.UnifyResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 幂等性 token 数据管理实现类
 */
@Service
public class IdempotentTokenSuperviseImpl implements IdempotentTokenSupervise {
    private static final Logger log = LoggerFactory.getLogger(IdempotentTokenSuperviseImpl.class);

    /* 简单的缓存实现 */
    private static final ConcurrentHashMap<String, String> IDEMPOTENT_CACHE = new ConcurrentHashMap<>(16);

    @Override
    public boolean cacheToken(String key, String token) {
        verifyParams(key, "Cache Idempotent Token Key Can't Be Empty");
        verifyParams(token, "Cache Idempotent Token Value Can't Be Empty");
        if (log.isDebugEnabled()) {
            log.debug("Cache idempotent token key [{}] value [{}]", key, token);
        }
        IDEMPOTENT_CACHE.put(key, token);
        return true;
    }

    @Override
    public boolean deleteToken(String key) {
        verifyParams(key, "Delete idempotent token key can't be empty");
        IDEMPOTENT_CACHE.remove(key);
        return true;
    }

    @Override
    public boolean exists(String key) {
        verifyParams(key, "Check idempotent token key exists can't be empty");
        return IDEMPOTENT_CACHE.contains(key);
    }

    private void verifyParams(String param, String errorTipMessage) {
        if (null == param || "".equals(param.trim())) {
            throw new IdempotentException(errorTipMessage, UnifyResultCode.PARAM_IS_BLANK);
        }
    }

}
