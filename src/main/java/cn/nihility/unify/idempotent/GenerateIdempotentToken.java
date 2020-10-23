package cn.nihility.unify.idempotent;

/**
 * 生成幂等性 token 的接口
 */
public interface GenerateIdempotentToken {

    /**
     * 生成幂等性校验 token
     * @return 生成好的幂等性唯一认证 token
     */
    String generateToken();

}
