package cn.nihility.common.util;

import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Apache Http 工具类
 */
public final class ApacheHttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(ApacheHttpClientUtil.class);

    private ApacheHttpClientUtil() {
    }

    /**
     * Scheme for HTTP based communication.
     */
    public static final String HTTP_SCHEME = "http";

    /**
     * Scheme for HTTPS based communication.
     */
    public static final String HTTPS_SCHEME = "https";

    private static final Timer connectionManagerTimer =
        new Timer("PoolingHttpClientConnectionManager.connectionManagerTimer", true);

    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 200;
    public static final int DEFAULT_MAX_CONNECTIONS_PER_HOST = 50;
    public static final long DEFAULT_POOL_KEEP_ALIVE_TIME = 15 * 60L;
    public static final TimeUnit DEFAULT_POOL_KEEP_ALIVE_TIME_UNITS = TimeUnit.SECONDS;
    public static final int DEFAULT_CONNECT_TIMEOUT = 2000;
    public static final int DEFAULT_READ_TIMEOUT = 2000;
    public static final Boolean DEFAULT_FOLLOW_REDIRECTS = Boolean.FALSE;
    public static final boolean DEFAULT_GZIP_PAYLOAD = true;
    public static final int DEFAULT_CONNECTION_IDLE_TIMER_TASK_REPEAT_IN_MSECS = 30000; // every half minute (30 secs)


    public static HttpClientConnectionManager createDefaultConnectionManager() {
        return newConnectionManager(false);
    }

    public static HttpClientConnectionManager newConnectionManager(boolean disableSslValidation) {
        return newConnectionManager(disableSslValidation, DEFAULT_MAX_TOTAL_CONNECTIONS, DEFAULT_MAX_CONNECTIONS_PER_HOST,
            DEFAULT_POOL_KEEP_ALIVE_TIME, DEFAULT_POOL_KEEP_ALIVE_TIME_UNITS, null);
    }

    public static HttpClientConnectionManager newConnectionManager(boolean disableSslValidation,
                                                                   int maxTotalConnections, int maxConnectionsPerRoute) {
        return newConnectionManager(disableSslValidation, maxTotalConnections, maxConnectionsPerRoute,
            -1, TimeUnit.MILLISECONDS, null);
    }

    public static HttpClientConnectionManager newConnectionManager(boolean disableSslValidation, int maxTotalConnections,
                                                                   int maxConnectionsPerRoute, long timeToLive, TimeUnit timeUnit,
                                                                   RegistryBuilder<ConnectionSocketFactory> registryBuilder) {
        if (registryBuilder == null) {
            registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create()
                .register(HTTP_SCHEME, PlainConnectionSocketFactory.INSTANCE);
        }

        if (disableSslValidation) {
            try {
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null,
                    new TrustManager[]{new DisabledValidationTrustManager()},
                    new SecureRandom());
                registryBuilder.register(HTTPS_SCHEME,
                    new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE));
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                logger.warn("Error creating SSLContext", e);
            }
        } else {
            registryBuilder.register(HTTPS_SCHEME, SSLConnectionSocketFactory.getSocketFactory());
        }

        final Registry<ConnectionSocketFactory> registry = registryBuilder.build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
            registry, null, null, null, timeToLive, timeUnit);
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);

        connectionManagerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                connectionManager.closeExpiredConnections();
            }
        }, 30000, DEFAULT_CONNECTION_IDLE_TIMER_TASK_REPEAT_IN_MSECS);

        return connectionManager;
    }

    public static void destroy() {
        connectionManagerTimer.cancel();
    }

    public static CloseableHttpClient createHttpClient(boolean disableSslValidation) {
        // disableContentCompression() 关闭压缩
        // disableCookieManagement() 关闭 Cookies 管理
        HttpClientBuilder httpClientFactory = HttpClientBuilder.create()
            .disableCookieManagement()
            .useSystemProperties();

        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
            .setRedirectsEnabled(DEFAULT_FOLLOW_REDIRECTS)
            .build();

        return httpClientFactory.setDefaultRequestConfig(defaultRequestConfig)
            .setConnectionManager(newConnectionManager(disableSslValidation))
            .build();
    }

    public static CloseableHttpClient createHttpClient(boolean disableSslValidation, CookieStore cookieStore) {
        HttpClientBuilder httpClientFactory = HttpClientBuilder.create().useSystemProperties();

        if (null == cookieStore) {
            // 关闭 Cookies 管理
            httpClientFactory.disableCookieManagement();
        } else {
            httpClientFactory.setDefaultCookieStore(cookieStore);
        }

        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
            .setRedirectsEnabled(DEFAULT_FOLLOW_REDIRECTS)
            .build();

        return httpClientFactory.setDefaultRequestConfig(defaultRequestConfig)
            .setConnectionManager(newConnectionManager(disableSslValidation))
            .build();
    }

    public static CloseableHttpClient createDefaultHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpClientBuilder httpClientFactory = builder.disableContentCompression()
            .disableCookieManagement()
            .useSystemProperties();

        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
            .setRedirectsEnabled(DEFAULT_FOLLOW_REDIRECTS)
            .build();

        return httpClientFactory.setDefaultRequestConfig(defaultRequestConfig)
            .setConnectionManager(createDefaultConnectionManager())
            .build();

    }

    public static RequestConfig buildRequestConfig() {
        return RequestConfig.custom()
            .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
            .setSocketTimeout(DEFAULT_READ_TIMEOUT)
            .setRedirectsEnabled(DEFAULT_FOLLOW_REDIRECTS)
            .setContentCompressionEnabled(DEFAULT_GZIP_PAYLOAD)
            .build();
    }

    static class DisabledValidationTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

}
