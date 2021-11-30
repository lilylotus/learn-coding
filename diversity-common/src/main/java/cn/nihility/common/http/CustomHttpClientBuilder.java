package cn.nihility.common.http;

import cn.nihility.common.util.IdleConnectionReaper;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

/**
 * 自定义 HttpClientBuilder
 */
public class CustomHttpClientBuilder extends HttpClientBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CustomHttpClientBuilder.class);

    public void closeRemoveConnectionManager(final HttpClientConnectionManager connectionManager) {
        addCloseable(new Closeable() {
            @Override
            public void close() {
                if (null != connectionManager) {
                    logger.info("HttpClient 关闭连带移除相应的 Idle 校验的 ConnectionManager");
                    IdleConnectionReaper.removeConnectionManager(connectionManager);
                }
            }
        });
    }

}
