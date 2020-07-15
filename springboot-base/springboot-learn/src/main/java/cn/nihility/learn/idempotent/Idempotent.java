package cn.nihility.learn.idempotent;

import javax.servlet.http.HttpServletRequest;

/**
 * idempotent token 处理接口
 *
 * @author dandelion
 * @date 2020-05-07 15:43
 */
public interface Idempotent {

    /**
     * 创建 token
     */
    String createToken();

    /**
     * 校验 token
     */
    boolean checkToken(HttpServletRequest request) throws Exception;

}
