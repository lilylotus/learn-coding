package cn.nihility.common.util;

import cn.nihility.common.http.CustomHttpClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;

public class DefaultHttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(DefaultHttpClientUtil.class);

    private DefaultHttpClientUtil() {
    }

    /**
     * Scheme for HTTP based communication.
     */
    public static final String HTTP_SCHEME = "http";

    /**
     * Scheme for HTTPS based communication.
     */
    public static final String HTTPS_SCHEME = "https";

    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = -1;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 50 * 1000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 50 * 1000;
    public static final int DEFAULT_MAX_CONNECTIONS = 1024;
    public static final long DEFAULT_CONNECTION_TTL = -1;
    public static final long DEFAULT_IDLE_CONNECTION_TIME = 60 * 1000L;
    public static final int DEFAULT_VALIDATE_AFTER_INACTIVITY = 2 * 1000;
    public static final int DEFAULT_THREAD_POOL_WAIT_TIME = 60 * 1000;
    public static final int DEFAULT_REQUEST_TIMEOUT = 5 * 60 * 1000;
    public static final long DEFAULT_SLOW_REQUESTS_THRESHOLD = 5 * 60 * 1000L;
    public static final boolean DEFAULT_USE_REAPER = true;

    private static HttpClientConnectionManager httpClientConnectionManager;
    private static CloseableHttpClient defaultHttpClient;

    public static HttpClientConnectionManager createHttpClientConnectionManager() {
        SSLContext sslContext;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
            NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register(HTTP_SCHEME, PlainConnectionSocketFactory.getSocketFactory())
            .register(HTTPS_SCHEME, sslSocketFactory)
            .build();

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
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS);
        connectionManager.setMaxTotal(DEFAULT_MAX_CONNECTIONS);
        connectionManager.setValidateAfterInactivity(DEFAULT_VALIDATE_AFTER_INACTIVITY);
        connectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(DEFAULT_SOCKET_TIMEOUT).setTcpNoDelay(true).build());

        IdleConnectionReaper.setIdleConnectionTime(DEFAULT_IDLE_CONNECTION_TIME);
        IdleConnectionReaper.registerConnectionManager(connectionManager);

        return connectionManager;
    }

    /* ============================== private, inner invoke ============================== */

    private static HttpClientConnectionManager createDefaultHttpClientConnectionManager() {
        HttpClientConnectionManager mgr = httpClientConnectionManager;
        if (mgr == null) {
            synchronized (DefaultHttpClientUtil.class) {
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
            synchronized (DefaultHttpClientUtil.class) {
                client = defaultHttpClient;
                if (client == null) {
                    defaultHttpClient = createHttpClient(createDefaultHttpClientConnectionManager());
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

    public static CloseableHttpClient createHttpClient(HttpClientConnectionManager connectionManager) {
        final CustomHttpClientBuilder builder = new CustomHttpClientBuilder();
        builder.closeRemoveConnectionManager(connectionManager);
        return builder.setConnectionManager(connectionManager)
            .setUserAgent(getDefaultUserAgent())
            .disableContentCompression()
            .disableAutomaticRetries()
            .build();
    }

    public static CloseableHttpClient createHttpClient() {
        return createHttpClient(createHttpClientConnectionManager());
    }

    public static RequestConfig createRequestConfig() {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        requestConfigBuilder.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT);
        requestConfigBuilder.setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT);
        return requestConfigBuilder.build();
    }

    public static void shutdown(HttpClientConnectionManager connectionManager) {
        if (null != connectionManager) {
            IdleConnectionReaper.removeConnectionManager(connectionManager);
            connectionManager.shutdown();
        }
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

    @SuppressWarnings("unchecked")
    public static <R> R executeHttpRequest(final CloseableHttpClient httpClient,
                                           final HttpUriRequest request,
                                           final Class<R> rt) {
        // 注意：HttpClient 池化管理，不需要关闭
        try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
            StatusLine statusLine = httpResponse.getStatusLine();
            String respStringEntity = EntityUtils.toString(httpResponse.getEntity());
            if (logger.isDebugEnabled()) {
                logger.debug("响应状态 [{}], 响应消息 [{}]", statusLine, respStringEntity);
            }
            if (StringUtils.isNotBlank(respStringEntity)) {
                return rt.isAssignableFrom(String.class) ?
                    (R) respStringEntity :
                    JacksonUtil.readJsonString(respStringEntity, rt);
            }
        } catch (IOException e) {
            logger.error("请求 [{}] 异常", request.getURI(), e);
        }
        return null;
    }

    public static <R> R executeHttpRequest(final HttpUriRequest request, Class<R> rt) {
        // 注意：HttpClient 池化管理，不需要关闭
        return executeHttpRequest(createDefaultHttpClient(), request, rt);
    }

    public static <R> R executePostRequestWithResult(final URI uri, final String jsonBody, Class<R> rt) {

        final HttpPost post = new HttpPost(uri);
        post.addHeader("Content-Type", "application/json");

        if (null != jsonBody) {
            StringEntity body = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            body.setContentEncoding("UTF-8");
            body.setContentType("application/json");
            post.setEntity(body);
        }
        return executeHttpRequest(post, rt);
    }

}
