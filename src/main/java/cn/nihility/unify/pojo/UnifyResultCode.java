package cn.nihility.unify.pojo;

import org.springframework.http.HttpStatus;

/**
 * 统一返回的状态码和对应的信息
 */
public enum  UnifyResultCode {
    /* ============ 2xx ： 请求已成功，请求所希望的响应头或数据体将随此响应返回 */
    SUCCESS(HttpStatus.OK, 200000, "success"),
    /* POST : 添加了新用户，上传了图片，创建了新活动 */
    CREATED(HttpStatus.CREATED, 201000, "创建数据成功"),
    /*
    * 服务器已经接受到了来自客户端的请求，但还未开始处理
    * 常用短信发送、邮件通知、模板消息推送等这类很耗时需要队列支持的场景中
    * 返回该状态码时，响应实体 必须 为空
    * */
    ACCEPTED(HttpStatus.ACCEPTED, 202000, "ACCEPTED"),
    /*
    * 该状态码表示响应实体不包含任何数据，其中：
    * 在使用 DELETE 方法删除资源 成功 时，必须 返回该状态码
    * 使用 PUT、PATCH 方法更新数据 成功 时，也 应该 返回此状态码
    * */
    NO_CONTENT(HttpStatus.NO_CONTENT, 204000, "NO_CONTENT"),

    /* ============ 3xx ： 重定向 */

    /* ============ 4xx ： 客户端原因引起的错误 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400000, "无效的签名"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401000, "Token Signature could not be verified."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403000, "权限不足"),

    /* ============ 5xx : 服务端原因引起的错误 */
    /*
     * 该状态码 必须 在服务器出错时抛出，对于所有的 500 错误，都 应该 提供完整的错误信息支持，也方便跟踪调试。
     * */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500000, "INTERNAL_SERVER_ERROR"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, 503000, "Service Unavailable"),

    /* ============ 6xx : 其它，自定义的状态码 */
    PARAM_IS_INVALID(HttpStatus.BAD_REQUEST, 60001, "参数无效"),
    PARAM_IS_BLANK(HttpStatus.BAD_REQUEST, 60002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(HttpStatus.BAD_REQUEST, 6003, "参数类型错误"),
    PARAM_NOT_COMPLETE(HttpStatus.BAD_REQUEST, 60004, "参数缺失"),
    TEST_UNIFY_EXCEPTION(HttpStatus.SERVICE_UNAVAILABLE, 60005, "测试统一异常处理"),
    REPETITIVE_OPERATION(HttpStatus.BAD_REQUEST, 60006, "重复操作"),
    PARAM_VALUE_EXPIRE_OR_NOT_EXIST(HttpStatus.BAD_REQUEST, 60006, "值过期或不存在")
    ;

    private HttpStatus httpStatus;
    private Integer code;
    private String message;

    UnifyResultCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
