package cn.nihility.common.constant;

/**
 * OAuth 2.0 Constant
 * https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1
 *
 * @author nihility
 * @date 2022/02/18 11:33
 */
public class Oauth2Constant {

    private Oauth2Constant() {
    }

    /**
     * 协议类型
     */
    public static final String PROTOCOL = "OAUTH";
    /**
     * 响应类型，必填
     */
    public static final String RESPONSE_TYPE = "response_type";
    /**
     * 授权码模式
     */
    public static final String RESPONSE_TYPE_CODE_VALUE = "code";
    /**
     * 隐式授权模式
     */
    public static final String RESPONSE_TYPE_IMPLICIT_VALUE = "token";
    /**
     * 应用 ID
     */
    public static final String CLIENT_ID = "client_id";
    /**
     * 重定向地址，可选
     */
    public static final String REDIRECT_URI = "redirect_uri";
    /**
     * 应用范围，可选
     */
    public static final String SCOPE = "scope";
    /**
     * 状态，推荐
     */
    public static final String STATE = "state";

    /**
     * 访问 token
     */
    public static final String ACCESS_TOKEN = "access_token";
    /**
     * REFRESH_TOKEN
     */
    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String REFRESH_TOKEN_PREFIX = "RT";

    public static final String ACCESS_TOKEN_PREFIX = "AT";

    public static final String IMPLICIT_EXPIRES_IN = "expires_in";

    public static final String TOKEN_TYPE = "token_type";

    public static final String TOKEN_TYPE_VALUE = "Bearer";

    public static final String GRANT_TYPE = "grant_type";

    public static final String GRANT_AUTHORIZATION_CODE_TYPE = "authorization_code";

    public static final String GRANT_REFRESH_TOKEN_GRANT_TYPE = "refresh_token";


}
