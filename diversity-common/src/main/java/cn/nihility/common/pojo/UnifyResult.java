package cn.nihility.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 统一 WEB 返回格式
 */
public class UnifyResult implements Serializable {

    private static final long serialVersionUID = 2525361212419774307L;

    /**
     * 响应码
     */
    private int code;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 返回数据
     */
    private transient Object data;
    /**
     * 错误异常堆栈信息
     */
    private List<StackTraceElement> stackTrace;

    public UnifyResult(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public UnifyResult(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public UnifyResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<StackTraceElement> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<StackTraceElement> stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Override
    public String toString() {
        return "UnifyResult{" +
            "code=" + code +
            ", message='" + message + '\'' +
            ", data=" + data +
            '}';
    }

}
