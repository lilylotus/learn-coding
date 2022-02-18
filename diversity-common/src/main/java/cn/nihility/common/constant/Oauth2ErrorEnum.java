package cn.nihility.common.constant;

/**
 * @author nihility
 * @date 2022/02/18 13:09
 */
public enum Oauth2ErrorEnum {
    
    /**
     * The request is missing a required parameter, includes an
     * invalid parameter value, includes a parameter more than
     * once, or is otherwise malformed.
     */
    INVALID_REQUEST("invalid_request", "请求参数不正确"),
    /**
     * The client is not authorized to request an authorization code using this method.
     */
    UNAUTHORIZED_CLIENT("unauthorized_client", "未认证客户端"),
    /**
     * The resource owner or authorization server denied the request.
     */
    ACCESS_DENIED("access_denied", "拒绝访问"),
    /**
     * The authorization server does not support obtaining an
     * authorization code using this method.
     */
    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type", "不支持的响应类型"),
    /**
     * The requested scope is invalid, unknown, or malformed.
     */
    INVALID_SCOPE("invalid_scope", "请求的范围无效、未知或格式错误"),
    /**
     * The authorization server encountered an unexpected
     * condition that prevented it from fulfilling the request.
     * (This error code is needed because a 500 Internal Server
     * Error HTTP status code cannot be returned to the client
     * via an HTTP redirect.)
     */
    SERVER_ERROR("server_error", "服务端异常"),
    /**
     * The authorization server is currently unable to handle
     * the request due to a temporary overloading or maintenance
     * of the server.  (This error code is needed because a 503
     * Service Unavailable HTTP status code cannot be returned
     * to the client via an HTTP redirect.)
     */
    TEMPORARILY_UNAVAILABLE("temporarily_unavailable", "服务暂时不可用");

    String code;
    String description;

    Oauth2ErrorEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
