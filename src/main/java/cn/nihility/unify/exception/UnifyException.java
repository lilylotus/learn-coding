package cn.nihility.unify.exception;

import cn.nihility.unify.pojo.UnifyResultCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 统一的异常类
 */
public class UnifyException extends RuntimeException {

    private static final long serialVersionUID = 342912393687438606L;

    private UnifyResultCode resultCode;
    /* 错误提示文本 */
    private String tipMessage;

    public UnifyException(String tipMessage) {
        super(tipMessage);
        this.tipMessage = tipMessage;
        this.resultCode = UnifyResultCode.INTERNAL_SERVER_ERROR;
    }

    public UnifyException(UnifyResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.tipMessage = resultCode.getMessage();
    }

    public UnifyException(String tipMessage, UnifyResultCode resultCode) {
        super(resultCode.getMessage());
        this.tipMessage = tipMessage;
        this.resultCode = resultCode;
    }

    public UnifyException(String tipMessage, Throwable cause, UnifyResultCode resultCode) {
        super(cause);
        this.tipMessage = tipMessage;
        this.resultCode = resultCode;
    }

    public UnifyResultCode getResultCode() {
        return resultCode;
    }

    public String getTipMessage() {
        return StringUtils.isBlank(this.tipMessage) ? super.getMessage() : this.tipMessage;
    }

    @Override
    public String toString() {
        return "UnifyException{" +
                "tipMessage='" + tipMessage + '\'' +
                '}';
    }
}
