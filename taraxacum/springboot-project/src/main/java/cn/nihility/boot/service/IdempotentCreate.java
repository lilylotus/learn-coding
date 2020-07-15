package cn.nihility.boot.service;

/**
 * idempotent token 创建，幂等性
 * @author dandelion
 * @date 2020:06:27 20:16
 */
public interface IdempotentCreate {

    /**
     * 创建 token
     */
    String createToken();

}
