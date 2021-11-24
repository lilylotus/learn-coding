package cn.nihility.api.service;

import cn.nihility.api.exception.IdempotentException;

/**
 * 幂等操作业务接口
 */
public interface IdempotentService {

    /**
     * 生成幂等性校验 token
     *
     * @return 生成好的幂等性唯一认证 token
     */
    String generateToken();

    /**
     * 校验幂等性 key 对应的 token 是否存在
     *
     * @param token 要校验的 token key
     * @return true -> 校验成功
     * @throws IdempotentException 幂等性校验异常
     */
    boolean verify(String token) throws IdempotentException;

}
