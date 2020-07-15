package cn.nihility.http;

import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpClientTest
 *
 * @author dandelion
 * @date 2020-06-26 11:52
 */
public class HttpClientTest {

    @Test
    public void testHttpClientGet() {
        String url = "https://www.baidu.com/";
        HttpClientFactory.doGet(url);
    }

    @Test
    public void testHttpClientGet1() {
        String url = "http://localhost:8080//mvc-servlet/my/hei";
        Map<String, String> headers = new HashMap<>();
        headers.put("user", "http client user");
        headers.put("password", "http client password");
        HttpGet httpGet = HttpClientFactory.createHttpGet(url, headers);
        HttpClientFactory.doGetRequest(httpGet);
    }

}
