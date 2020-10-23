package cn.nihility.unify.idempotent.impl;


import cn.nihility.unify.exception.IdempotentException;
import cn.nihility.unify.idempotent.GenerateIdempotentToken;
import cn.nihility.unify.idempotent.IdempotentTokenSupervise;
import cn.nihility.unify.idempotent.VerifyIdempotentToken;
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
public class IdempotentServiceImpl implements GenerateIdempotentToken, VerifyIdempotentToken {

    private static final Logger log = LoggerFactory.getLogger(IdempotentServiceImpl.class);

    private final IdempotentTokenSupervise idempotentTokenSupervise;

    public IdempotentServiceImpl(IdempotentTokenSupervise idempotentTokenSupervise) {
        this.idempotentTokenSupervise = idempotentTokenSupervise;
    }

    @Override
    public String generateToken() {
        return SnowflakeIdWorker.nextLongStringId();
    }

    @Override
    public boolean verify(String token) throws IdempotentException {
        if (StringUtils.isBlank(token)) {
            throw new IdempotentException("Idempotent verify token cannot be blank", UnifyResultCode.UNAUTHORIZED);
        }

        if (!idempotentTokenSupervise.exists(token)) {
            throw new IdempotentException("Idempotent token expire", UnifyResultCode.INTERNAL_SERVER_ERROR);
        }

        return true;
    }

}
