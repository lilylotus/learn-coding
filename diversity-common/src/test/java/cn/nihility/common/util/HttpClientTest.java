package cn.nihility.common.util;

import cn.nihility.common.constant.RequestMethodEnum;
import cn.nihility.common.pojo.ResponseHolder;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpClientTest {

    @Test
    void testHttpsGet() {
//        MDC.put(Constant.TRACE_ID, UuidUtils.jdkUUID());

        HttpGet request = new HttpGet("https://blog.csdn.net/lxj_1993/article/details/109451567");
        /*String result = HttpClientUtils.executeHttpRequest(request, String.class);
        System.out.println(result);*/

        HttpClientContext ctx = HttpClientUtils.createHttpClientContext();

        ResponseHolder<String> result1 = HttpClientUtils.executeRequestWithResponse(request, String.class);
        System.out.println(result1);
        result1.getCookies().forEach((k, v) -> System.out.println(k + "==" + v));

        HttpClientUtils.setContextThreadLocal(ctx);
        String resultString = HttpClientUtils.executeHttpRequest(request, String.class);
        System.out.println("=============");
        System.out.println(resultString);

        ctx.getCookieStore().getCookies().forEach(ck -> System.out.println(ck.getName() + "=" + ck.getValue()));
    }

    @Test
    void testNormalHttpClientRequestGet() throws IOException {
        final HttpClientConnectionManager connectionManager = HttpClientUtils.createHttpClientConnectionManager();
        final CloseableHttpClient httpClient = HttpClientUtils.createHttpClient(connectionManager);

        HttpGet httpRequest = new HttpGet("http://www.baidu.com");

        final BasicCookieStore cookieStore = new BasicCookieStore();
        HttpClientContext httpContext = HttpClientContext.create();
        httpContext.setRequestConfig(HttpClientUtils.createRequestConfig());
        httpContext.setCookieStore(cookieStore);

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpRequest, httpContext);
        } catch (IOException ex) {
            httpRequest.abort();
            ex.printStackTrace();
        }

        assertNotNull(httpResponse);
        final StatusLine statusLine = httpResponse.getStatusLine();
        assertNotNull(statusLine);
        assertEquals(200, statusLine.getStatusCode());
        System.out.println("响应状态为:" + statusLine);
        System.out.println("响应消息为:" + EntityUtils.toString(httpResponse.getEntity()));

        for (Cookie cookie : cookieStore.getCookies()) {
            System.out.println(cookie.getPath() + ":" + cookie.getName() + ":" + cookie.getValue());
        }

        httpResponse.close();
        httpClient.close();
        HttpClientUtils.shutdown(connectionManager);

    }

    @Test
    void testNormalPost() {
        final HttpClientConnectionManager connectionManager = HttpClientUtils.createHttpClientConnectionManager();

        final HttpPost post = new HttpPost("http://10.0.41.80:50012/manage-central/umm/v1/admin/login");
        post.addHeader("Content-Type", "application/json");

        StringEntity stringEntity = new StringEntity("{\"username\":\"系统管理员\",\"password\":\"Aa12345678\"}",
            StandardCharsets.UTF_8);
        stringEntity.setContentEncoding("UTF-8");
        stringEntity.setContentType("application/json");

        post.setEntity(stringEntity);
        post.setConfig(HttpClientUtils.createRequestConfig());

        try (final CloseableHttpClient httpClient = HttpClientUtils.createHttpClient(connectionManager)) {
            try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                final StatusLine statusLine = httpResponse.getStatusLine();
                assertNotNull(statusLine);
                assertEquals(201, statusLine.getStatusCode());
                System.out.println("响应状态为:" + statusLine);
                System.out.println("响应消息为:" + EntityUtils.toString(httpResponse.getEntity()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpClientUtils.shutdown(connectionManager);
    }

    @Test
    void testPostFormData2() {

        Map<String, String> params = new HashMap<>(4);
        params.put("userName", "admin");
        params.put("password", "123456");

        BasicCookieStore cookieStore = new BasicCookieStore();
        HttpClientContext httpContext = HttpClientContext.create();
        httpContext.setCookieStore(cookieStore);

        HttpUriRequest request = HttpClientUtils.buildFormHttpRequest("http://10.0.249.190:81/xxl-job-admin/login",
            RequestMethodEnum.POST, params);

        try (CloseableHttpClient httpClient = HttpClientUtils.createHttpClient(false)) {
            @SuppressWarnings("unchecked")
            Map<String, String> result = HttpClientUtils.executeHttpRequest(httpClient, request, Map.class);
            Assertions.assertNotNull(result);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Cookies:");
        cookieStore.getCookies().forEach(cookie -> System.out.println(cookie.getName() + " : " + cookie.getValue()));

    }

    @Test
    void testPostFormData() {
        BasicCookieStore cookieStore = new BasicCookieStore();
        HttpClientContext httpContext = HttpClientContext.create();
        httpContext.setCookieStore(cookieStore);

        try (CloseableHttpClient httpClient = HttpClientUtils.createHttpClient(true)) {

            HttpPost httpPost = new HttpPost("http://10.0.249.190:81/xxl-job-admin/login");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            // 将参数放入键值对类 NameValuePair 中,再放入集合中
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("userName", "admin"));
            params.add(new BasicNameValuePair("password", "123456"));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
            httpPost.setEntity(formEntity);

            try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost, httpContext)) {

                Assertions.assertEquals(200, httpResponse.getStatusLine().getStatusCode());

                System.out.println("响应状态为:" + httpResponse.getStatusLine());
                System.out.println("响应消息为:" + EntityUtils.toString(httpResponse.getEntity()));
                System.out.println(httpResponse.getFirstHeader("Set-Cookie").getValue());
            }

            System.out.println("Cookies:");
            cookieStore.getCookies().forEach(cookie -> System.out.println(cookie.getName() + " : " + cookie.getValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetResultString() {
        HttpGet get = new HttpGet("http://127.0.0.1:30010/login/auth/user");
        get.addHeader("Authorization", "xxx");
        String result = HttpClientUtils.executeHttpRequest(get, String.class);
        Assertions.assertNotNull(result);
        System.out.println(result);

        result = HttpClientUtils.executeHttpRequest(get, String.class);
        System.out.println(result);

        result = HttpClientUtils.executeHttpRequest(get, String.class);
        System.out.println(result);

        result = HttpClientUtils.executeHttpRequest(get, String.class);
        System.out.println(result);

        result = HttpClientUtils.executeHttpRequest(get, String.class);
        System.out.println(result);
    }

    @Test
    void testGetResultString2() {
        HttpGet get = new HttpGet("http://127.0.0.1:30010/login/auth/user");
        get.addHeader("Authorization", "xxx");
        CloseableHttpClient httpClient = HttpClientUtils.createHttpClient();
        String result = HttpClientUtils.executeHttpRequest(httpClient, get, String.class);
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void testHttpGetRequest() {
        String value = "kv";
        String url = HttpRequestUtils.addUrlParam("http://127.0.0.1:30040/welcome/get", "key", value);
        HttpGet get = new HttpGet(url);
        HttpGet get2 = new HttpGet(url);

        HashMap result = (HashMap) HttpClientUtils.executeHttpRequest(get, Map.class);
        Assertions.assertEquals(value, ((Map) result.get("data")).get("key"));

        HashMap result2 = (HashMap) HttpClientUtils.executeHttpRequest(get2, Map.class);
        Assertions.assertEquals(value, ((Map) result2.get("data")).get("key"));
    }

    @Test
    void testHttpGetRequestWithCookie() {

        HttpClientContext httpContext = HttpClientUtils.createHttpClientContext();
        CookieStore cookieStore = httpContext.getCookieStore();

        String value = "kv";
        String url = HttpRequestUtils.addUrlParam("http://127.0.0.1:30040/welcome/get", "key", value);
        HttpGet get = new HttpGet(url);
        HttpGet get2 = new HttpGet(url);

        HashMap result = (HashMap) HttpClientUtils.executeHttpRequest(get, Map.class);
        Assertions.assertEquals(value, ((Map) result.get("data")).get("key"));
        System.out.println("cookies1:");
        cookieStore.getCookies().forEach(cookie -> System.out.println(cookie.getName() + " : " + cookie.getValue()));

        HashMap result2 = (HashMap) HttpClientUtils.executeHttpRequest(get2, Map.class);
        Assertions.assertEquals(value, ((Map) result2.get("data")).get("key"));
        System.out.println("cookies2:");
        cookieStore.getCookies().forEach(cookie -> System.out.println(cookie.getName() + " : " + cookie.getValue()));
    }

}
