package cn.nihility.common.http;

import cn.nihility.common.entity.AuthenticateSession;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/17 16:59
 */
@Setter
@Getter
@ToString
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

}
