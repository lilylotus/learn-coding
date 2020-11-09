package cn.nihility.unify.pojo;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

/**
 * 统一错误信息
 * {
 *     "message": "直接展示给终端用户的错误信息",
 *     "error_code": "业务错误码",
 *     "error": "供开发者查看的错误信息",
 *     "debug": [
 *         "错误堆栈，必须开启 debug 才存在"
 *     ]
 * }
 */
public class UnifyResultError implements Serializable {
    private static final long serialVersionUID = 2356663357501106960L;

    private String message;
    private Integer error_code;
    private String error;
    private List<StackTraceElement> debug;
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public UnifyResultError() {
    }

    /**
     * 统一异常信息返回， 自定义错误提示文本
     * @param tipMessage 供展示的错误文本
     */
    public UnifyResultError(String tipMessage) {
        this.message = tipMessage;
        this.error_code = UnifyResultCode.INTERNAL_SERVER_ERROR.getCode();
        this.error = UnifyResultCode.INTERNAL_SERVER_ERROR.getMessage();
    }

    /**
     * 统一异常信息返回，无错误提示文本
     * @param resultCode 统一的返回状态码, 对应 5xx http 状态码
     */
    public UnifyResultError(UnifyResultCode resultCode) {
        this(resultCode, resultCode.getMessage());
    }

    /**
     * 统一异常信息返回，自定义统一异常码和提示信息
     * @param resultCode 统一返回码， 5xx 系列
     * @param tipMessage 错误提示文本
     */
    public UnifyResultError(UnifyResultCode resultCode, String tipMessage) {
        this.message = tipMessage;
        this.error_code = resultCode.getCode();
        this.error = resultCode.getMessage();
        this.httpStatus = resultCode.getHttpStatus();
    }

    /**
     * 统一异常信息返回，可自定义提示文本，错误码，错误 debug 提示文本
     * @param tipMessage 展示错误文本
     * @param error_code 错误码 5xx
     * @param error 内部调试提示文本
     */
    public UnifyResultError(String tipMessage, Integer error_code, String error) {
        this.message = tipMessage;
        this.error_code = error_code;
        this.error = error;
    }

    /**
     * 统一异常信息返回，自定义错误码，错误 debug 提示文本， 错误堆栈信息 （配置文件开启 DEBUG 展示）
     * @param resultCode 统一异常错误码
     * @param tipMessage 错误提示文本
     * @param debug 错误异常堆栈信息
     */
    public UnifyResultError(UnifyResultCode resultCode, String tipMessage, List<StackTraceElement> debug) {
        this.message = tipMessage;
        this.error_code = resultCode.getCode();
        this.error = resultCode.getMessage();
        this.debug = debug;
        this.httpStatus = resultCode.getHttpStatus();
    }

    public String getMessage() {
        return message;
    }

    public Integer getError_code() {
        return error_code;
    }

    public String getError() {
        return error;
    }

    public List<StackTraceElement> getDebug() {
        return debug;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setDebug(List<StackTraceElement> debug) {
        this.debug = debug;
    }
}
