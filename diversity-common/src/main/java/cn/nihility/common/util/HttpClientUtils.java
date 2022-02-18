package cn.nihility.common.util;

import cn.nihility.common.constant.RequestMethodEnum;
import cn.nihility.common.exception.HttpRequestException;
import cn.nihility.common.http.CustomHttpClientBuilder;
import cn.nihility.common.pojo.ResponseHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    private HttpClientUtils() {
    }

    /**
     * Scheme for HTTP based communication.
     */
    public static final String HTTP_SCHEME = "http";

    /**
     * Scheme for HTTPS based communication.
     */
    public static final String HTTPS_SCHEME = "https";

    public static final int DEFAULT_CONNECTION_TIMEOUT = 3000;
    /**
     * 等待数据的时间或者两个包之间的间隔时间
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 5000;

    /**
     * Default value for max number od connections.
     */
    public static final int DEFAULT_MAX_CONNECTIONS = 200;
    /**
     * Default value for max number od connections per route.
     */
    public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 50;
    /**
     * Default value for time to live.
     */
    public static final long DEFAULT_TIME_TO_LIVE = 900L;
    /**
     * Default time to live unit.
     */
    public static final TimeUnit DEFAULT_TIME_TO_LIVE_UNIT = TimeUnit.SECONDS;

    public static final long DEFAULT_IDLE_CONNECTION_TIME = 30000L;

    private static HttpClientConnectionManager httpClientConnectionManager;
    private static CloseableHttpClient defaultHttpClient;

    public static HttpClientConnectionManager createHttpClientConnectionManager(boolean disableSslValidation) {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create()
            .register(HTTP_SCHEME, PlainConnectionSocketFactory.INSTANCE);
        if (disableSslValidation) {
            try {
                // SSL
                final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null,
                    new TrustManager[]{new DisabledValidationTrustManager()},
                    new SecureRandom());
                registryBuilder.register(HTTPS_SCHEME, new SSLConnectionSocketFactory(
                    sslContext, NoopHostnameVerifier.INSTANCE));
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                logger.warn("Error creating SSLContext", e);
            }
        } else {
            registryBuilder.register(HTTPS_SCHEME, SSLConnectionSocketFactory.getSocketFactory());
        }
        // 1. 创建连接池管理器, 默认同时支持 HTTP/HTTPS
        /*
         * ClientConnectionPoolManager 会维护每个路由维护和最大连接数限制。
         * 默认情况下，此实现将为每个给定路由创建不超过 2 个并发连接，并且总共不超过 20 个连接。
         * 可以自由来调整连接限制。
         *
         * 另外构造函数中可以设置持久链接的存活时间 TTL（timeToLive），
         * 其定义了持久连接的最大使用时间，超过其 TTL 值的链接不会再被复用。
         *
         * 1.1 设置 TTL 为 60s（tomcat 服务器默认支持保持 60s 的连接，超过 60s，会关闭客户端的连接）
         * 1.2 设置连接器最多同时支持 1000 个链接
         * 1.3 设置每个路由最多支持 50 个链接。注意这里路由是指 IP+PORT 或者指域名。
         * (如果使用域名来访问则每个域名有自己的链接池，如果使用 IP+PORT 访问，则每个 IP+PORT 有自己的链接池)
         *
         * 默认注入了 PoolingHttpClientConnectionManager.getDefaultRegistry() -> http/https 支持
         * */
        final Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
            registry, null, null, null, DEFAULT_TIME_TO_LIVE, DEFAULT_TIME_TO_LIVE_UNIT);

        connectionManager.setMaxTotal(DEFAULT_MAX_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);

        //connectionManager.setValidateAfterInactivity(DEFAULT_VALIDATE_AFTER_INACTIVITY);
        //connectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(DEFAULT_SOCKET_TIMEOUT).setTcpNoDelay(true).build());

        return connectionManager;
    }

    public static HttpClientConnectionManager createHttpClientConnectionManager() {
        return createHttpClientConnectionManager(false);
    }

    /* ============================== private, inner invoke ============================== */

    public static void shutdown(HttpClientConnectionManager connectionManager) {
        if (null != connectionManager) {
            IdleConnectionReaper.removeConnectionManager(connectionManager);
            connectionManager.shutdown();
        }
    }

    private static HttpClientConnectionManager createDefaultHttpClientConnectionManager() {
        HttpClientConnectionManager mgr = httpClientConnectionManager;
        if (mgr == null) {
            synchronized (HttpClientUtils.class) {
                mgr = httpClientConnectionManager;
                if (mgr == null) {
                    final HttpClientConnectionManager instance = createHttpClientConnectionManager();
                    mgr = instance;
                    httpClientConnectionManager = instance;
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        logger.info("关闭默认的 HttpClient 连接管理工具");
                        shutdown(instance);
                    }));
                }
            }
        }
        return mgr;
    }

    private static CloseableHttpClient createDefaultHttpClient() {
        CloseableHttpClient client = defaultHttpClient;
        if (client == null) {
            synchronized (HttpClientUtils.class) {
                client = defaultHttpClient;
                if (client == null) {
                    defaultHttpClient = createHttpClient(createDefaultHttpClientConnectionManager(), true);
                    client = defaultHttpClient;
                }
            }
        }
        return client;
    }

    /* ============================== public, outer invoke ============================== */

    public static String getDefaultUserAgent() {
        return "HttpClient/v1.0(" + System.getProperty("os.name") + "/"
            + System.getProperty("os.version") + "/" + System.getProperty("os.arch") + ";"
            + System.getProperty("java.version") + ")";
    }

    public static CloseableHttpClient createHttpClient(HttpClientConnectionManager connectionManager,
                                                       boolean disableCookieManagement) {
        final CustomHttpClientBuilder builder = new CustomHttpClientBuilder();
        builder.closeRemoveConnectionManager(connectionManager);

        IdleConnectionReaper.setIdleConnectionTime(DEFAULT_IDLE_CONNECTION_TIME);
        IdleConnectionReaper.registerConnectionManager(connectionManager);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
            .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
            // 禁用自动重定向
            .setRedirectsEnabled(false)
            .build();

        builder.setConnectionManager(connectionManager)
            .setUserAgent(getDefaultUserAgent())
            .setDefaultRequestConfig(defaultRequestConfig)
            .disableContentCompression()
            .useSystemProperties();

        if (disableCookieManagement) {
            builder.disableCookieManagement();
        }

        return builder.build();
    }

    public static CloseableHttpClient createHttpClient(HttpClientConnectionManager connectionManager) {
        return createHttpClient(connectionManager, true);
    }

    public static CloseableHttpClient createHttpClient(boolean disableCookieManagement) {
        return createHttpClient(createHttpClientConnectionManager(), disableCookieManagement);
    }

    public static CloseableHttpClient createHttpClient() {
        return createHttpClient(true);
    }

    public static RequestConfig createRequestConfig() {
        RequestConfig.Builder config = RequestConfig.custom();
        config.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        config.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT);
        config.setRedirectsEnabled(true);
        return config.build();
    }

    public static HttpClientContext createHttpClientContext(CookieStore cookieStore) {
        if (cookieStore == null) {
            cookieStore = new BasicCookieStore();
        }
        HttpClientContext httpContext = HttpClientContext.create();
        httpContext.setRequestConfig(createRequestConfig());
        httpContext.setCookieStore(cookieStore);
        return httpContext;
    }

    public static HttpClientContext createHttpClientContext() {
        return createHttpClientContext(null);
    }

    public static HttpUriRequest buildHttpRequest(String url, RequestMethodEnum method) {
        CheckUtils.stringNotBlank(url, "请求 url 不可为空");
        URI uri = HttpRequestUtils.buildUri(url);
        switch (method) {
            case GET:
                return new HttpGet(uri);
            case POST:
                return new HttpPost(uri);
            case PUT:
                return new HttpPut(uri);
            case DELETE:
                return new HttpDelete(uri);
            default:
                return new HttpGet(url);
        }
    }

    public static HttpUriRequest buildJsonHttpRequest(String url, RequestMethodEnum method, Map<String, String> bodyParams) {
        HttpUriRequest request = buildHttpRequest(url, method);
        if (method != RequestMethodEnum.GET) {
            HttpRequestUtils.setApplicationJsonHeader(request);
            StringEntity entity = HttpRequestUtils.buildJsonStringEntity(JacksonUtils.toJsonString(bodyParams));
            HttpRequestUtils.setEntity((HttpEntityEnclosingRequestBase) request, entity);
        }
        return request;
    }

    public static HttpUriRequest buildFormHttpRequest(String url, RequestMethodEnum method, Map<String, String> bodyParams) {
        HttpUriRequest request = buildHttpRequest(url, method);
        if (method != RequestMethodEnum.GET) {
            HttpRequestUtils.setFormHeader(request);
            UrlEncodedFormEntity entity = HttpRequestUtils.buildUrlEncodedFormEntity(bodyParams);
            HttpRequestUtils.setEntity((HttpEntityEnclosingRequestBase) request, entity);
        }
        return request;
    }

    /* ------------------------------ 发送 http 请求 ------------------------------ */

    @SuppressWarnings("unchecked")
    public static <R> ResponseHolder<R> executeRequestWithResponse(final CloseableHttpClient httpClient,
                                                                   final HttpUriRequest request,
                                                                   final HttpContext httpContext,
                                                                   final Class<R> rt) {
        ResponseHolder<R> result = new ResponseHolder<>();
        // 注意：HttpClient 池化管理，不需要关闭
        try (CloseableHttpResponse httpResponse = httpClient.execute(request, httpContext)) {
            StatusLine statusLine = httpResponse.getStatusLine();
            String respStringEntity = EntityUtils.toString(httpResponse.getEntity());
            if (logger.isDebugEnabled()) {
                logger.debug("请求 [{}], 响应状态 [{}], 响应消息 [{}]",
                    request.getURI(), statusLine.getStatusCode(), respStringEntity);
            }
            result.setHeaders(HttpRequestUtils.headersToMap(httpResponse.getAllHeaders()));
            result.setStatusCode(statusLine.getStatusCode());
            R resultObject = null;
            if (StringUtils.isNotBlank(respStringEntity)) {
                resultObject = rt.isAssignableFrom(String.class) ?
                    (R) respStringEntity :
                    JacksonUtils.readJsonString(respStringEntity, rt);
            }
            result.setContent(resultObject);
        } catch (IOException e) {
            logger.error("请求 [{}] 异常", request.getURI());
            throw new HttpRequestException("请求 [" + request.getURI() + "] 异常", e);
        }
        return result;
    }

    public static <R> R executeHttpRequest(final CloseableHttpClient httpClient,
                                           final HttpUriRequest request,
                                           final HttpContext httpContext,
                                           final Class<R> rt) {
        return executeRequestWithResponse(httpClient, request, httpContext, rt).getContent();
    }

    public static <R> ResponseHolder<R> executeRequestWithResponse(final CloseableHttpClient httpClient,
                                                                   final HttpUriRequest request,
                                                                   final Class<R> rt) {
        return executeRequestWithResponse(httpClient, request, null, rt);
    }

    public static <R> R executeHttpRequest(final CloseableHttpClient httpClient,
                                           final HttpUriRequest request,
                                           final Class<R> rt) {
        return executeRequestWithResponse(httpClient, request, null, rt).getContent();
    }

    public static <R> ResponseHolder<R> executeRequestWithResponse(HttpUriRequest request, Class<R> rt) {
        // 注意：HttpClient 池化管理，不需要关闭
        return executeRequestWithResponse(createDefaultHttpClient(), request, rt);
    }

    public static <R> R executeHttpRequest(HttpUriRequest request, Class<R> rt) {
        return executeRequestWithResponse(request, rt).getContent();
    }

    public static <R> ResponseHolder<R> executeJsonRequestWithResponse(String url, RequestMethodEnum method,
                                                                       Map<String, String> bodyParams, Class<R> rt) {
        HttpUriRequest request = buildJsonHttpRequest(url, method, bodyParams);
        return executeRequestWithResponse(request, rt);
    }

    public static <R> R executeJsonRequest(String url, RequestMethodEnum method,
                                           Map<String, String> bodyParams, Class<R> rt) {
        return executeJsonRequestWithResponse(url, method, bodyParams, rt).getContent();
    }

    public static <R> ResponseHolder<R> executePostJsonRequestWithResponse(String url,
                                                                           Map<String, String> bodyParams,
                                                                           Class<R> rt) {
        return executeJsonRequestWithResponse(url, RequestMethodEnum.POST, bodyParams, rt);
    }

    public static <R> R executePostJsonRequest(String url, Map<String, String> bodyParams, Class<R> rt) {
        return executeJsonRequest(url, RequestMethodEnum.POST, bodyParams, rt);
    }

    public static <R> ResponseHolder<R> executeFormRequestWithResponse(String url, RequestMethodEnum method,
                                                                       Map<String, String> bodyParams, Class<R> rt) {
        HttpUriRequest request = buildFormHttpRequest(url, method, bodyParams);
        return executeRequestWithResponse(request, rt);
    }

    public static <R> R executeFormRequest(String url, RequestMethodEnum method,
                                           Map<String, String> bodyParams, Class<R> rt) {
        return executeFormRequestWithResponse(url, method, bodyParams, rt).getContent();
    }

    public static <R> ResponseHolder<R> executePostFormRequestWithResponse(String url,
                                                                           Map<String, String> bodyParams,
                                                                           Class<R> rt) {
        return executeFormRequestWithResponse(url, RequestMethodEnum.POST, bodyParams, rt);
    }

    public static <R> R executePostFormRequest(String url, Map<String, String> bodyParams, Class<R> rt) {
        return executeFormRequest(url, RequestMethodEnum.POST, bodyParams, rt);
    }

    static class DisabledValidationTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

}
