package cn.nihility.api.constant;

/**
 * 常量类
 */
public final class Constant {

    private Constant() {
    }

    /**
     * 幂等性 token 键值
     */
    public static final String IDEMPOTENT_TOKEN_KEY = "idempotent";

    /**
     * 身份认证 token key
     */
    public static final String AUTHENTICATION_TOKEN_KEY = "Authorization";

    /**
     * 身份认证 Bearer token 前缀
     */
    public static final String AUTHENTICATION_BEARER_TOKEN_PREFIX = "Bearer ";

}
