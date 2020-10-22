package cn.nihility.unify.pojo;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 统一返回的数据格式
 */
public class UnifyResult implements Serializable {
    private static final long serialVersionUID = 2525361212419774307L;

    private Integer code;
    /* 前端展示使用 */
    private String message;
    /* 供调试提示的信息 */
    private String tipMessage;
    private Object data;
    private HttpStatus httpStatus = HttpStatus.OK;

    public UnifyResult() {
    }

    public UnifyResult(UnifyResultCode resultCode) {
        this(resultCode, null);
    }

    public UnifyResult(UnifyResultCode resultCode, Object data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.tipMessage = resultCode.getMessage();
        this.data = data;
        this.httpStatus = resultCode.getHttpStatus();
    }

    public UnifyResult(UnifyResultCode resultCode, String tipMessage, Object data) {
        this.code = resultCode.getCode();
        this.message = tipMessage;
        this.tipMessage = resultCode.getMessage();
        this.data = data;
        this.httpStatus = resultCode.getHttpStatus();
    }

    public UnifyResult(Integer code, String tipMessage, Object data) {
        this(code, tipMessage, tipMessage, data);
    }

    public UnifyResult(Integer code, String message, String tipMessage, Object data) {
        this.code = code;
        this.message = message;
        this.tipMessage = tipMessage;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getTipMessage() {
        return tipMessage;
    }
}
