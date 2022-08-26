package cn.nihility.common.http;

import cn.nihility.common.constant.Constant;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.common.util.NetworkUtils;
import cn.nihility.common.util.UuidUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author nihility
 * @date 2022/02/17 16:59
 */
public class RequestContextHolder {

    private static final ThreadLocal<RequestContext> REQUEST_CONTEXT = ThreadLocal.withInitial(RequestContext::new);

    private RequestContextHolder() {
    }

    /**
     * 获取当前线程请求的上下文内容
     *
     * @return 请求上下文内容
     */
    public static RequestContext getContext() {
        return REQUEST_CONTEXT.get();
    }

    /**
     * 移除 ThreadLocal 的对象
     */
    public static void removeContext() {
        REQUEST_CONTEXT.remove();
    }

    /**
     * 装配请求上下文内容
     * @param request 请求
     * @param context 请求上下文
     */
    public static void assembleContext(HttpServletRequest request, RequestContext context) {

        String traceId = Optional.ofNullable(request.getHeader(Constant.TRACE_ID)).orElse(UuidUtils.jdkUuid());
        context.setTraceId(traceId);
        context.setRequest(request);
        context.setStart(System.currentTimeMillis());
        context.setRequestUri(HttpRequestUtils.getOriginatingRequestUri(request));
        context.setRequestMethod(request.getMethod());
        context.setIp(NetworkUtils.obtainRequestIp(request));

    }

    public static void assembleContext(HttpServletRequest request) {
        assembleContext(request, getContext());
    }

}
