package cn.nihility.common.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * 统一返回基础消息格式
 *
 * @date 2022/02/11 15:03
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnifyBaseResult implements Serializable {

    private static final long serialVersionUID = -2477851610755982193L;

    /**
     * 响应码
     */
    private Integer code;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 服务器的时间戳
     */
    private Long timestamp;
    /**
     * 错误异常堆栈信息
     */
    private List<StackTraceElement> stackTrace;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<StackTraceElement> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<StackTraceElement> stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Override
    public String toString() {
        return "UnifyBaseResult{" +
            "code=" + code +
            ", message='" + message + '\'' +
            ", timestamp=" + timestamp +
            ", stackTrace=" + stackTrace +
            '}';
    }

}
