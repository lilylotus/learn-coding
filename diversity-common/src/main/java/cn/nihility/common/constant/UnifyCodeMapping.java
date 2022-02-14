package cn.nihility.common.constant;

/**
 * 统一返回的状态码和对应的信息
 */
public enum UnifyCodeMapping {
    /* ============ 2xx ： 请求已成功，请求所希望的响应头或数据体将随此响应返回 */
    SUCCESS(20000, "success"),
    /* POST : 添加了新用户，上传了图片，创建了新活动 */
    CREATED(20100, "创建数据成功"),
    /*
     * 服务器已经接受到了来自客户端的请求，但还未开始处理
     * 常用短信发送、邮件通知、模板消息推送等这类很耗时需要队列支持的场景中
     * 返回该状态码时，响应实体 必须 为空
     * */
    ACCEPTED(20200, "ACCEPTED"),
    /*
     * 该状态码表示响应实体不包含任何数据，其中：
     * 在使用 DELETE 方法删除资源 成功 时，必须 返回该状态码
     * 使用 PUT、PATCH 方法更新数据 成功 时，也 应该 返回此状态码
     * */
    NO_CONTENT(20400, "NO_CONTENT"),

    /* ============ 3xx ： 重定向 */

    /* ============ 4xx ： 客户端原因引起的错误 */
    BAD_REQUEST(40000, "无效的签名"),
    UNAUTHORIZED(40100, "Token Signature could not be verified."),
    FORBIDDEN(40300, "权限不足"),


    /* ============ 5xx : 服务端原因引起的错误 */
    /*
     * 该状态码 必须 在服务器出错时抛出，对于所有的 500 错误，都 应该 提供完整的错误信息支持，也方便跟踪调试。
     * */
    INTERNAL_SERVER_ERROR(50000, "INTERNAL_SERVER_ERROR"),
    SERVICE_UNAVAILABLE(50300, "Service Unavailable"),

    /* ============ 6xx : 其它，自定义的状态码 */
    /* 60000 - 61000 : params/value exception */
    PARAM_IS_INVALID(60001, "参数无效"),
    PARAM_IS_BLANK(60002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(60003, "参数类型错误"),
    PARAM_NOT_COMPLETE(60004, "参数缺失"),
    PARAM_VALUE_EXPIRE_OR_NOT_EXIST(60005, "值过期或不存在"),

    /* 61001 - 62000 : operation exception */
    TEST_UNIFY_EXCEPTION(61001, "测试统一异常处理"),
    REPETITIVE_OPERATION(61002, "重复操作");

    /* 自定义的状态码 */
    private Integer code;
    /* 提示信息 */
    private String message;

    UnifyCodeMapping(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "UnifyCodeMapping{" +
            "code=" + code +
            ", message='" + message + '\'' +
            '}';
    }
}
