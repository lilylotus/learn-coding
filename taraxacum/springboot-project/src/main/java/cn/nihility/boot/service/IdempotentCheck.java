package cn.nihility.boot.service;

import javax.servlet.http.HttpServletRequest;

/**
 * idempotent token 创建，幂等性 校验
 * @author dandelion
 * @date 2020:06:27 20:17
 */
public interface IdempotentCheck {

    /**
     * 校验 token
     */
    boolean checkToken(HttpServletRequest request) throws Exception;

}
