package cn.nihility.unify.interceptor;

import cn.nihility.unify.constant.Constants;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.slf4j.MDC;

import java.io.IOException;

/**
 * 添加 HttpClient 拦截器
 */
public class HttpClientTraceIdInterceptor implements HttpRequestInterceptor {
    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        String traceId = MDC.get(Constants.TRACE_ID);
        // 当前线程调用中有 traceId，则将该 traceId 进行透传
        if (traceId != null) {
            //添加请求体
            request.addHeader(Constants.TRACE_ID, traceId);
        }
    }
}
