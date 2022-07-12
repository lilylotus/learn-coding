package cn.nihility.common.http;

import cn.nihility.common.constant.Constant;
import cn.nihility.common.util.IdleConnectionReaper;
import cn.nihility.common.util.UuidUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Objects;

/**
 * 自定义 HttpClientBuilder
 */
public class CustomHttpClientBuilder extends HttpClientBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CustomHttpClientBuilder.class);

    private boolean disabledCookieManagement;

    public void closeRemoveConnectionManager(final HttpClientConnectionManager connectionManager) {
        addCloseable(() -> {
            if (null != connectionManager) {
                logger.info("HttpClient 关闭连带移除相应的 Idle 校验的 ConnectionManager");
                IdleConnectionReaper.removeConnectionManager(connectionManager);
            }
        });
    }

    public HttpClientBuilder customDisableCookieManagement() {
        disabledCookieManagement = true;
        return disableCookieManagement();
    }

    @Override
    public CloseableHttpClient build() {
        addInterceptorFirst(new HttpRequestTraceInterceptor());
        if (disabledCookieManagement) {
            addInterceptorFirst(new RequestInitCookies());
            addInterceptorLast(new ResponseProcessCookies());
        }
        addInterceptorLast(new HttpResponseTraceInterceptor());
        return super.build();
    }

    static class HttpRequestTraceInterceptor implements HttpRequestInterceptor {

        @Override
        public void process(HttpRequest request, HttpContext context) {
            String traceId = MDC.get(Constant.TRACE_ID);
            if (StringUtils.isBlank(traceId)) {
                traceId = UuidUtils.jdkUUID();
                context.setAttribute(Constant.TRACE_ID, traceId);
            }
        }

    }

    static class HttpResponseTraceInterceptor implements HttpResponseInterceptor {

        @Override
        public void process(HttpResponse response, HttpContext context) {
            if (!Objects.isNull(context.getAttribute(Constant.TRACE_ID))) {
                context.removeAttribute(Constant.TRACE_ID);
            }
        }

    }

}
