package cn.nihility.boot.controller.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author dandelion
 * @date 2020:06:27 09:53
 */
public class ResultVo implements Serializable {
    private static final long serialVersionUID = -1255075500169226221L;

    private Integer code;
    private String message;
    private LocalDateTime dateTime = LocalDateTime.now();

    private ResultErrorInfo errorInfo;
    private Object data;

    public ResultVo() {
    }

    /* ====== common ====== */
    public ResultVo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultVo(ResultState state) {
        convertStatus(state);
    }

    /* ====== success ====== */
    public ResultVo(ResultState state, Object data) {
        convertStatus(state);
        this.data = data;
    }

    public ResultVo(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /* ====== error ====== */
    public ResultVo(ResultState state, ResultErrorInfo errorInfo) {
        convertStatus(state);
        this.errorInfo = errorInfo;
    }

    public ResultVo(Integer code, String message, ResultErrorInfo errorInfo) {
        this.code = code;
        this.message = message;
        this.errorInfo = errorInfo;
    }

    private void convertStatus(ResultState state) {
        if (null == state) return;

        this.code = state.getCode();
        this.message = state.getMessage();
    }

    public ResultErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(ResultErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
