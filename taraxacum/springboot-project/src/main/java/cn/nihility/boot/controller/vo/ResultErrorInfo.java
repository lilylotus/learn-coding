package cn.nihility.boot.controller.vo;

/**
 * @author dandelion
 * @date 2020:06:27 09:54
 */
public class ResultErrorInfo {

    private String time;//发生时间
    private String url;//访问Url
    private String error;//错误类型
    private String stackTrace;//错误的堆栈轨迹
    private Integer statusCode;//状态码
    private String reasonPhrase;//错误阶段

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }
}
