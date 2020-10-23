package cn.nihility.unify.idempotent.impl;


import cn.nihility.unify.exception.IdempotentException;
import cn.nihility.unify.idempotent.IdempotentService;
import cn.nihility.unify.idempotent.IdempotentTokenSupervise;
import cn.nihility.unify.pojo.UnifyResultCode;
import cn.nihility.unify.util.SnowflakeIdWorker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 幂等性业务处理实现类
 */
@Service
public class IdempotentServiceImpl implements IdempotentService {

    private static final Logger log = LoggerFactory.getLogger(IdempotentServiceImpl.class);

    private final IdempotentTokenSupervise idempotentTokenSupervise;

    public IdempotentServiceImpl(IdempotentTokenSupervise idempotentTokenSupervise) {
        this.idempotentTokenSupervise = idempotentTokenSupervise;
    }

    @Override
    public String generateToken() {
        String token = SnowflakeIdWorker.nextLongStringId();
        idempotentTokenSupervise.cacheToken(token, token);
        return token;
    }

    @Override
    public boolean verify(String tokenKey) throws IdempotentException {
        if (StringUtils.isBlank(tokenKey)) {
            throw new IdempotentException("Idempotent verify token key cannot be blank", UnifyResultCode.PARAM_IS_BLANK);
        }
        /* 校验该 token 是否存在，可能被消费掉了或过期了 */
        if (!idempotentTokenSupervise.exists(tokenKey)) {
            throw new IdempotentException("Idempotent token expire or consumed", UnifyResultCode.PARAM_IS_INVALID);
        }
        /* 删除该 token */
        boolean del = idempotentTokenSupervise.deleteToken(tokenKey);
        if (!del) {
            /* 删除失败，表示在此期间被别的服务消费掉了 */
            throw new IdempotentException(UnifyResultCode.REPETITIVE_OPERATION);
        }
        return true;
    }

}
