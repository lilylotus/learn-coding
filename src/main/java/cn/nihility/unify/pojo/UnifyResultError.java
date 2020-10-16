package cn.nihility.unify.pojo;

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

    public UnifyResultError() {
    }

    public UnifyResultError(String tipMessage) {
        this.message = tipMessage;
        this.error_code = UnifyResultCode.INTERNAL_SERVER_ERROR.getCode();
        this.error = UnifyResultCode.INTERNAL_SERVER_ERROR.getMessage();
    }

    public UnifyResultError(UnifyResultCode resultCode) {
        this.error = resultCode.getMessage();
        this.error_code = resultCode.getCode();
    }

    public UnifyResultError(UnifyResultCode resultCode, String tipMessage) {
        this.message = tipMessage;
        this.error_code = resultCode.getCode();
        this.error = resultCode.getMessage();
    }

    public UnifyResultError(String tipMessage, Integer error_code, String error) {
        this.message = tipMessage;
        this.error_code = error_code;
        this.error = error;
    }

    public UnifyResultError(UnifyResultCode resultCode, String tipMessage, List<StackTraceElement> debug) {
        this.message = tipMessage;
        this.error_code = resultCode.getCode();
        this.error = resultCode.getMessage();
        this.debug = debug;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getError_code() {
        return error_code;
    }

    public void setError_code(Integer error_code) {
        this.error_code = error_code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<StackTraceElement> getDebug() {
        return debug;
    }

    public void setDebug(List<StackTraceElement> debug) {
        this.debug = debug;
    }
}
