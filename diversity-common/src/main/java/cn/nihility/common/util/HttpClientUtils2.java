package cn.nihility.common.util;

import cn.nihility.common.constant.HttpMethod;
import cn.nihility.common.exception.HttpClientException;
import cn.nihility.common.http.CustomHttpClientBuilder;
import cn.nihility.common.pojo.ResponseHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
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
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * http 请求工具类
 *
 * @author nihility
 * @date 2023-04-11 22:10
 */
public class HttpClientUtils2 {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

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
     * 从连接池中获取 connections 的超时时间
     */
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 1000;
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
    public static final long EVICT_IDLE_CONNECT_TIME_OUT = 30L;
    /**
     * Default time to live unit.
     */
    public static final TimeUnit DEFAULT_TIME_TO_LIVE_UNIT = TimeUnit.SECONDS;

    public static final long DEFAULT_IDLE_CONNECTION_TIME = 30000L;

    /**
     * 客户端证书路径，用了本地绝对路径，需要修改
     */
    private static final String CLIENT_CERT_FILE = "C:\\Users\\intel\\Desktop\\ssl2\\client\\client.p12";
    /**
     * 客户端证书密码
     */
    private static final String CLIENT_PWD = "123456";
    /**
     * 信任库路径，即 keytool 生成的那个自定义名称的库文件
     */
    private static final String TRUST_STORE_FILE = "C:\\Users\\intel\\Desktop\\ssl2\\ca\\test.truststore";
    /**
     * 信任库密码，即 keytool 时的密码
     */
    private static final String TRUST_STORE_PWD = "123456";

    private HttpClientUtils2() {
    }

    private static volatile HttpClientConnectionManager httpClientConnectionManager;

    private static volatile CloseableHttpClient defaultHttpClient;

    private static KeyStore loadKeyStore(String keyStorePath, String password, String type)
        throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(type);
        try (FileInputStream is = new FileInputStream(keyStorePath)) {
            ks.load(is, password.toCharArray());
        }
        return ks;
    }

    /**
     * ssl 双向认证
     *
     * @return ssl context
     */
    public static SSLContext createSslContext() {
        SSLContext ctx = null;
        try {
            // 初始化密钥库
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = loadKeyStore(CLIENT_CERT_FILE, CLIENT_PWD, "PKCS12");
            keyManagerFactory.init(keyStore, CLIENT_PWD.toCharArray());

            // 初始化信任库
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            KeyStore trustKeyStore = loadKeyStore(TRUST_STORE_FILE, TRUST_STORE_PWD, "JKS");
            trustManagerFactory.init(trustKeyStore);

            // 初始化SSL上下文
            ctx = SSLContext.getInstance("TLSv1.2");
            // keyManagerFactory.getKeyManagers() 双向认证客户端证书，服务端 ca 证书校验客户端 client.cer/client.key
            // trustManagerFactory.getTrustManagers()  ca 证书，校验服务端 server.cer/server.key
            ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        } catch (Exception ex) {
            log.error("create https ssl context error", ex);
        }
        return ctx;
    }

    public static HttpClientConnectionManager createHttpClientConnectionManager(boolean sslValidation) {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create()
            .register(HTTP_SCHEME, PlainConnectionSocketFactory.INSTANCE);
        if (sslValidation) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null,
                    new TrustManager[]{new DisabledValidationTrustManager()},
                    new SecureRandom());
                registryBuilder.register(HTTPS_SCHEME, new SSLConnectionSocketFactory(
                    sslContext, NoopHostnameVerifier.INSTANCE));
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                log.warn("Error creating SSLContext", e);
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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("关闭默认的 HttpClient 连接管理工具");
            shutdown(connectionManager);
        }));

        return connectionManager;
    }

    /* ============================== private, inner invoke ============================== */

    public static void shutdown(HttpClientConnectionManager connectionManager) {
        if (null != connectionManager) {
            //IdleConnectionReaper.removeConnectionManager(connectionManager);
            connectionManager.shutdown();
        }
    }

    private static synchronized HttpClientConnectionManager defaultHttpClientConnectionManager() {
        if (httpClientConnectionManager == null) {
            httpClientConnectionManager = createHttpClientConnectionManager(true);
        }
        return httpClientConnectionManager;
    }

    private static synchronized CloseableHttpClient defaultHttpClient() {
        if (defaultHttpClient == null) {
            HttpClientConnectionManager cm = defaultHttpClientConnectionManager();
            defaultHttpClient = createHttpClient(cm, true);
            //IdleConnectionReaper.registerConnectionManager(cm);
        }
        return defaultHttpClient;
    }

    /* ============================== public, outer invoke ============================== */

    private static String getDefaultUserAgent() {
        return "HttpClient/v1.0(" + System.getProperty("os.name") + "/"
            + System.getProperty("os.version") + "/" + System.getProperty("os.arch") + ";"
            + System.getProperty("java.version") + ")";
    }

    /**
     * 创建 Http 请求 Client
     *
     * @param connectionManager       http 请求连接管理器
     * @param disableCookieManagement 禁用 cookie 管理
     * @return HttpClient
     */
    public static CloseableHttpClient createHttpClient(HttpClientConnectionManager connectionManager,
                                                       boolean disableCookieManagement) {
        final CustomHttpClientBuilder builder = new CustomHttpClientBuilder();
        if (null == connectionManager) {
            connectionManager = createHttpClientConnectionManager(true);
        }
        builder.closeRemoveConnectionManager(connectionManager);

        // ConnectTimeout：连接建立时间，三次握手完成时间
        // SocketTimeout：指的是等待数据的时间，或者两次数据包之间的时间间隔。
        // 值表示的是 “a”、”b”、”c” 这三个报文，每两个相邻的报文的间隔时间不能超过 SocketTimeout
        RequestConfig defaultRequestConfig = RequestConfig.custom()
            // 获取请求数据超时时间（响应时间），单位毫秒
            .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
            // 连接超时时间，单位毫秒
            .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
            // 从连接池中获取 connection 超时时间，单位毫秒
            .setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT)
            .setRedirectsEnabled(DEFAULT_FOLLOW_REDIRECTS)
            // cookie
            .setCookieSpec(CookieSpecs.STANDARD)
            .build();

        builder.setConnectionManager(connectionManager)
            .setDefaultRequestConfig(defaultRequestConfig)
            // MaxIdleTime 必须小于服务端的关闭时间，否则可能出现 NoHttpResponse，关闭闲置连接
            .evictIdleConnections(EVICT_IDLE_CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .evictExpiredConnections()
            .setUserAgent(getDefaultUserAgent());

        // useSystemProperties()
        // disableContentCompression()

        if (disableCookieManagement) {
            builder.customDisableCookieManagement();
        }

        return builder.build();
    }

    /**
     * 获取 HttpClient
     *
     * @return HttpClient
     */
    public static CloseableHttpClient obtainHttpClient() {
        return createHttpClient(createHttpClientConnectionManager(true), true);
    }

    public static HttpClientContext createHttpClientContext() {
        HttpClientContext httpContext = HttpClientContext.create();
        httpContext.setCookieStore(new BasicCookieStore());
        return httpContext;
    }

    public static HttpUriRequest buildHttpRequest(String url, HttpMethod method) {
        CheckUtils.stringNotBlank(url, "请求 url 不可为空");
        URI uri = HttpUtils.buildUri(url);
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

    /**
     * 构建 json 请求
     *
     * @param url        http 请求 url
     * @param method     请求方法
     * @param bodyParams 请求参数
     * @return http json 请求
     */
    public static HttpUriRequest buildJsonHttpRequest(String url, HttpMethod method, Map<String, String> bodyParams) {
        HttpUriRequest request = buildHttpRequest(url, method);
        if (method != HttpMethod.GET) {
            HttpUtils.setApplicationJsonHeader(request);
            StringEntity entity = HttpUtils.buildJsonStringEntity(JacksonUtils.toJsonString(bodyParams));
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
        return request;
    }

    /**
     * 构建 form 表单请求
     *
     * @param url        http 请求 url
     * @param method     请求方法
     * @param formParams 请求参数
     * @return http form 请求
     */
    public static HttpUriRequest buildFormHttpRequest(String url, HttpMethod method, Map<String, String> formParams) {
        HttpUriRequest request = buildHttpRequest(url, method);
        if (method != HttpMethod.GET) {
            HttpUtils.setFormHeader(request);
            UrlEncodedFormEntity entity = HttpUtils.buildUrlEncodedFormEntity(formParams);
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
        return request;
    }

    /* ------------------------------ 发送 http 请求 ------------------------------ */

    @SuppressWarnings("unchecked")
    public static <R> ResponseHolder<R> sendRequest(HttpUriRequest request, Class<R> rt) {
        final ResponseHolder<R> result = new ResponseHolder<>();
        HttpClientContext ctx = createHttpClientContext();
        long startMillis = System.currentTimeMillis();

        // 注意：HttpClient 池化管理，不需要关闭
        CloseableHttpClient httpClient = defaultHttpClient();
        try (CloseableHttpResponse httpResponse = httpClient.execute(request, ctx)) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String respStringEntity = EntityUtils.toString(httpResponse.getEntity());
            if (log.isDebugEnabled()) {
                log.debug("请求 [{}], 响应状态 [{}], 响应消息 [{}]", request.getURI(), statusCode, respStringEntity);
            }
            result.setHeaders(HttpUtils.headersToMap(httpResponse.getAllHeaders()));
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

            Optional.ofNullable(ctx.getCookieStore().getCookies())
                .orElse(Collections.emptyList())
                .forEach(ck -> result.addCookie(ck.getName(), ck.getValue()));
        } catch (IOException e) {
            log.error("HttpClient request [{}] with exception, start [{}], duration [{}]ms",
                request.getURI(), startMillis, (System.currentTimeMillis() - startMillis));
            throw new HttpClientException("请求 [" + request.getURI() + "] 异常", e);
        }
        if (log.isDebugEnabled()) {
            log.debug("send [{}] request duration [{}] ms", request.getURI(), (System.currentTimeMillis() - startMillis));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <R> R sendRequestSimple(HttpUriRequest request, Class<R> rt) {
        R result = null;
        long startMillis = System.currentTimeMillis();

        // 注意：HttpClient 池化管理，不需要关闭
        CloseableHttpClient httpClient = defaultHttpClient();
        try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String respStringEntity = EntityUtils.toString(httpResponse.getEntity());
            if (log.isDebugEnabled()) {
                log.debug("请求 [{}], 状态码 [{}], 响应 [{}]", request.getURI(), statusCode, respStringEntity);
            }
            if (HttpStatus.SC_OK == statusCode && StringUtils.isNotBlank(respStringEntity)) {
                result = rt.isAssignableFrom(String.class) ? (R) respStringEntity :
                    JacksonUtils.readJsonString(respStringEntity, rt);
            }
        } catch (IOException e) {
            log.error("HttpClient request [{}] with exception, start [{}], duration [{}]ms",
                request.getURI(), startMillis, (System.currentTimeMillis() - startMillis));
            throw new HttpClientException("请求 [" + request.getURI() + "] 异常", e);
        }
        if (log.isDebugEnabled()) {
            log.debug("send [{}] request duration [{}] ms", request.getURI(), (System.currentTimeMillis() - startMillis));
        }

        return result;
    }

    public static <T> T sendGetRequest(String url, Map<String, String> param, Class<T> rt) {
        URI uri = HttpUtils.buildUri(url, param);
        return sendRequestSimple(new HttpGet(uri), rt);
    }

    public static <T> T sendJsonRequest(String url, HttpMethod method, Map<String, String> body, Class<T> rt) {
        HttpUriRequest request = buildJsonHttpRequest(url, method, body);
        return sendRequestSimple(request, rt);
    }

    public static <T> T sendFormRequest(String url, HttpMethod method, Map<String, String> form, Class<T> rt) {
        HttpUriRequest request = buildFormHttpRequest(url, method, form);
        return sendRequestSimple(request, rt);
    }

    static class DisabledValidationTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // Noncompliant, nothing means trust any client
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // Noncompliant, this method never throws exception, it means trust any server
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
