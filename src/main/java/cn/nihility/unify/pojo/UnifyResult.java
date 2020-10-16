package cn.nihility.unify.pojo;

import java.io.Serializable;

/**
 * 统一返回的数据格式
 */
public class UnifyResult implements Serializable {
    private static final long serialVersionUID = 2525361212419774307L;

    private Integer code;
    private String message;
    private Object data;

    public UnifyResult() {
    }

    public UnifyResult(UnifyResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public UnifyResult(UnifyResultCode resultCode, Object data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public UnifyResult(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
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

}
