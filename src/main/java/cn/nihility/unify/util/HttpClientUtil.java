package cn.nihility.unify.util;

import cn.nihility.unify.interceptor.HttpClientTraceIdInterceptor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HTTP Client 工具类
 */
public class HttpClientUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final CloseableHttpClient HTTP_CLIENT =
            HttpClientBuilder.create().addInterceptorFirst(new HttpClientTraceIdInterceptor()).build();


    public static String doGet(String uri) {
        log.info("Http Client Do Get URI [{}]", uri);
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = null;

        try {
            response = HTTP_CLIENT.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = null;
        try {
            if (null != response) {
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Http Client Do Get Response [{}]", result);
        return result;
    }

}
