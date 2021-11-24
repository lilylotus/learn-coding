package cn.nihility.plugin.log4j2.interceptor;

import cn.nihility.plugin.log4j2.constant.Constant;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestTemplateTraceIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String traceId = MDC.get(Constant.TRACE_ID);
        if (traceId != null) {
            request.getHeaders().add(Constant.TRACE_ID, traceId);
        }
        return execution.execute(request, body);
    }

}
