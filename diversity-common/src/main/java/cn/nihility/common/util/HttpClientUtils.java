package cn.nihility.common.util;

import cn.nihility.common.constant.RequestMethodEnum;
import cn.nihility.common.exception.HttpClientException;
import cn.nihility.common.http.CustomHttpClientBuilder;
import cn.nihility.common.pojo.ResponseHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
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
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
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

    /**
     * 连接建立时间，三次握手完成时间
     */
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
     * Default value for following redirects.
     */
    public static final boolean DEFAULT_FOLLOW_REDIRECTS = true;
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

    private static HttpClientConnectionManager defaultHttpClientConnectionManager() {
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

    private static CloseableHttpClient defaultHttpClient() {
        CloseableHttpClient client = defaultHttpClient;
        if (client == null) {
            synchronized (HttpClientUtils.class) {
                client = defaultHttpClient;
                if (client == null) {
                    HttpClientConnectionManager cm = defaultHttpClientConnectionManager();
                    defaultHttpClient = createHttpClient(cm, true);
                    IdleConnectionReaper.registerConnectionManager(cm);
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

        // ConnectTimeout：连接建立时间，三次握手完成时间
        // SocketTimeout：指的是等待数据的时间，或者两次数据包之间的时间间隔。
        // 值表示的是 “a”、”b”、”c” 这三个报文，每两个相邻的报文的间隔时间不能超过 SocketTimeout
        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
            .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
            .setRedirectsEnabled(DEFAULT_FOLLOW_REDIRECTS)
            .build();

        builder.setConnectionManager(connectionManager)
            .setUserAgent(getDefaultUserAgent())
            .setDefaultRequestConfig(defaultRequestConfig)
            .setRetryHandler(new StandardHttpRequestRetryHandler(3, true));
        // useSystemProperties()
        // disableContentCompression()

        if (disableCookieManagement) {
            builder.customDisableCookieManagement();
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
        return RequestConfig.custom()
            .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
            .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
            .setConnectionRequestTimeout(DEFAULT_CONNECTION_TIMEOUT)
            .build();
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
        HttpUriRequest request;
        switch (method) {
            case GET:
                request = new HttpGet(uri);
                break;
            case POST:
                request = new HttpPost(uri);
                break;
            case PUT:
                request = new HttpPut(uri);
                break;
            case DELETE:
                request = new HttpDelete(uri);
                break;
            default:
                request = new HttpGet(url);
        }
        return request;
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
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String respStringEntity = EntityUtils.toString(httpResponse.getEntity());
            if (logger.isDebugEnabled()) {
                logger.debug("请求 [{}], 响应状态 [{}], 响应消息 [{}]",
                    request.getURI(), statusCode, respStringEntity);
            }
            result.setHeaders(HttpRequestUtils.headersToMap(httpResponse.getAllHeaders()));
            result.setStatusCode(statusCode);
            if (HttpStatus.SC_OK == statusCode) {
                if (StringUtils.isNotBlank(respStringEntity)) {
                    R resultObject = rt.isAssignableFrom(String.class) ?
                        (R) respStringEntity :
                        JacksonUtils.readJsonString(respStringEntity, rt);
                    result.setContent(resultObject);
                }
            } else {
                result.setErrorContent(respStringEntity);
            }
        } catch (IOException e) {
            logger.error("请求 [{}] 异常", request.getURI());
            throw new HttpClientException("请求 [" + request.getURI() + "] 异常", e);
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
        return executeRequestWithResponse(defaultHttpClient(), request, rt);
    }

    public static <R> ResponseHolder<R> executeRequestWithResponse(HttpUriRequest request, Class<R> rt, HttpContext httpContext) {
        // 注意：HttpClient 池化管理，不需要关闭
        return executeRequestWithResponse(defaultHttpClient(), request, httpContext, rt);
    }

    public static <R> R executeHttpRequest(HttpUriRequest request, Class<R> rt) {
        return executeRequestWithResponse(request, rt).getContent();
    }

    public static <R> R executeHttpRequest(HttpUriRequest request, Class<R> rt, HttpContext httpContext) {
        return executeRequestWithResponse(request, rt, httpContext).getContent();
    }

    /**
     * 执行请求 header 内容类型为 application/json 的请求，GET 方式除外
     */
    public static <R> ResponseHolder<R> executeJsonWithResponse(String url, RequestMethodEnum method,
                                                                Map<String, String> bodyParams, Class<R> rt) {
        HttpUriRequest request = buildJsonHttpRequest(url, method, bodyParams);
        return executeRequestWithResponse(request, rt);
    }

    public static <R> R executeJsonRequest(String url, RequestMethodEnum method,
                                           Map<String, String> bodyParams, Class<R> rt) {
        return executeJsonWithResponse(url, method, bodyParams, rt).getContent();
    }

    public static <R> ResponseHolder<R> executePostJsonWithResponse(String url,
                                                                    Map<String, String> bodyParams,
                                                                    Class<R> rt) {
        return executeJsonWithResponse(url, RequestMethodEnum.POST, bodyParams, rt);
    }

    public static <R> R executePostJson(String url, Map<String, String> bodyParams, Class<R> rt) {
        return executeJsonRequest(url, RequestMethodEnum.POST, bodyParams, rt);
    }

    public static <R> ResponseHolder<R> executeFormWithResponse(String url, RequestMethodEnum method,
                                                                Map<String, String> bodyParams, Class<R> rt) {
        HttpUriRequest request = buildFormHttpRequest(url, method, bodyParams);
        return executeRequestWithResponse(request, rt);
    }

    public static <R> R executeForm(String url, RequestMethodEnum method,
                                    Map<String, String> formParams, Class<R> rt) {
        return executeFormWithResponse(url, method, formParams, rt).getContent();
    }

    /**
     * 执行 POST form 表单请求，返回指定类型的数据 + 请求响应的 headers 和 status 数据
     */
    public static <R> ResponseHolder<R> executePostFormWithResponse(String url,
                                                                    Map<String, String> bodyParams,
                                                                    Class<R> rt) {
        return executeFormWithResponse(url, RequestMethodEnum.POST, bodyParams, rt);
    }

    /**
     * 执行 POST form 表单请求，返回指定类型的数据
     */
    public static <R> R executePostForm(String url, Map<String, String> bodyParams, Class<R> rt) {
        return executeForm(url, RequestMethodEnum.POST, bodyParams, rt);
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
