package cn.nihility.common.constant;


/**
 * @author nihility
 */
public class Constant {

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
    /**
     * 日志 Trace ID
     */
    public static final String TRACE_ID = "TraceId";

    public static final String UTF8 = "UTF-8";

    public static final String RESPONSE_HEADER_REDIRECT = "Location";

    public static final int RESPONSE_REDIRECT_STATUS_VALUE = 302;

}
