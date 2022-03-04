package cn.nihility.common.http;

import cn.nihility.common.entity.AuthenticateSession;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/17 16:59
 */
public class RequestContext {

    private String ctxId;
    /**
     * 请求开始时间戳
     */
    private long start;
    /**
     * 请求结束时间戳
     */
    private long end;
    /**
     * 请求链路追踪 ID
     */
    private String traceId;
    /**
     * 当前上下文请求
     */
    private HttpServletRequest request;
    /**
     * 请求类型
     */
    private String requestMethod;
    /**
     * 请求 uri
     */
    private String requestUri;
    /**
     * 请求 ip
     */
    private String ip;
    /**
     * 响应状态码
     */
    private int statusCode;
    /**
     * 是否已经登录过
     */
    private boolean loginBefore;
    /**
     * 额外属性保存
     */
    private Map<String, Object> extAttrs;
    /**
     * 认证会话
     */
    private AuthenticateSession authSession;

    /* ============================== getter/setter ============================== */

    public String getCtxId() {
        return ctxId;
    }

    public void setCtxId(String ctxId) {
        this.ctxId = ctxId;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isLoginBefore() {
        return loginBefore;
    }

    public void setLoginBefore(boolean loginBefore) {
        this.loginBefore = loginBefore;
    }

    public Map<String, Object> getExtAttrs() {
        return extAttrs;
    }

    public void setExtAttrs(Map<String, Object> extAttrs) {
        this.extAttrs = extAttrs;
    }

    public AuthenticateSession getAuthSession() {
        return authSession;
    }

    public void setAuthSession(AuthenticateSession authSession) {
        this.authSession = authSession;
    }
}
