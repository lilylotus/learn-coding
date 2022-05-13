package cn.nihility.common.constant;

public class OidcConstant {

    private OidcConstant() {
    }

    public static final String PROTOCOL = "OIDC";

    /**
     * REQUIRED.
     * code: Authorization Code Grant
     * token: Implicit Grant
     */
    public static final String RESPONSE_TYPE_FILED = "response_type";

    public static final String AUTHORIZATION_CODE_VALUE = "code";

    public static final String IMPLICIT_GRANT_VALUE = "token";

    public static final String RESPONSE_ACCESS_TOKEN_FIELD = "access_token";

    public static final String RESPONSE_ID_TOKEN_FIELD = "id_token";

    public static final String RESPONSE_TOKEN_TYPE_FIELD = "token_type";

    public static final String TOKEN_TYPE_VALUE = "Bearer";

    public static final String RESPONSE_EXPIRES_IN_FIELD = "expires_in";

    public static final String TOKEN_REQUEST_GRANT_TYPE_FIELD = "grant_type";

    public static final String TOKEN_REQUEST_CODE_FIELD = "code";

    public static final String TOKEN_REQUEST_GRANT_CODE_TYPE_VALUE = "authorization_code";

    public static final String TOKEN_REQUEST_GRANT_REFRESH_TYPE_VALUE = "refresh_token";

    /**
     * OpenID - REQUIRED, Oauth2.0 - OPTIONAL
     * <p>
     * OpenID Connect requests MUST contain the openid scope value.
     */
    public static final String SCOPE_FIELD = "scope";

    public static final String OPENID_SCOPE_VALUE = "openid";

    /**
     * REQUIRED. OAuth 2.0 Client Identifier valid at the Authorization Server.
     */
    public static final String CLIENT_ID_FIELD = "client_id";

    /**
     * REQUIRED. Redirection URI to which the response will be sent.
     */
    public static final String REDIRECT_URI_FIELD = "redirect_uri";

    /**
     * RECOMMENDED.
     * <p>
     * Opaque value used to maintain state between the request and the callback.
     * Cross-Site Request Forgery (CSRF, XSRF) mitigation is done by cryptographically
     * binding the value of this parameter with a browser cookie.
     */
    public static final String STATE_FIELD = "state";

    /* ====== 与协议无关常量 */

    public static final String OIDC_TOKEN_PREFIX = "oidc";

    public static final String ACCESS_TOKEN_PREFIX = "AT";

    public static final String REFRESH_TOKEN_PREFIX = "RT";

    public static final String CODE_TOKEN_PREFIX = "CT";

    public static final String AUTHORIZE_TOKEN_TYPE = "authorize";

    public static final String TOKEN_TOKEN_TYPE = "token";

    public static final String AUTHORIZE_IMPLICIT_TOKEN_TYPE = "implicit";

    public static final String SCOPE_VALUE = "openid";

    public static final long AUTHORIZE_CODE_TTL = 600L;

    public static final long ACCESS_TOKEN_TTL = 3600L;

    public static final long REFRESH_TOKEN_TTL = 86400L;

}
