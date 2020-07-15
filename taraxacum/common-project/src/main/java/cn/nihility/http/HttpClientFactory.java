package cn.nihility.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * HttpFactory
 *
 * @author dandelion
 * @date 2020-06-26 11:48
 */
public class HttpClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

    public static void doGet(String url) {
        // 创建请求对象和响应对象
        // CloseableHttpClient httpClient = HttpClients.createDefault();
        // CloseableHttpResponse response = null;

        // 1. 创建 http get 请求
        HttpGet httpGet = createHttpGet(url, null);
        doGetRequest(httpGet);
    }

    public static HttpGet createHttpGet() {
        return createHttpGet(null, null);
    }

    public static HttpGet createHttpGet(String url, Map<String, String> headers) {
        // 1. 创建 http get 请求
        HttpGet httpGet = new HttpGet();

        // 配置请求信息
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout( 50000)
                .setConnectionRequestTimeout(50000)
                .setSocketTimeout(50000)
                .setRedirectsEnabled(true)
                .build();

        // 引用请求配置
        httpGet.setConfig(requestConfig);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.79 Safari/537.36");
        // httpGet.addHeader("set-cookie", "AG_Token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjlkNTZmZGQ0LTkwM2EtM2M3YS1iMzJlLWIxNDFjOGU5NGU1YyIsImFjYyI6MjY0OTkyLCJpYXQiOjE1NzgzMDEzNjUsImV4cCI6MTU4MDg5MzM2NH0.IjHThqfu7ByqfqBDHP5a-7oRLzJW8RlugfieFUs7w8I; Domain=.appgrowing.cn; expires=Wed, 05-Feb-2020 09:02:44 GMT; Max-Age=2591999; Path=/");

        if (headers != null) {
            headers.forEach(httpGet::addHeader);
        }

        if (StringUtils.isNotBlank(url)) {
            httpGet.setURI(URI.create(url));
        }

        /*BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();*/

        return httpGet;
    }

    public static void writeFile(String data, String fileLocation) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(fileLocation, true), StandardCharsets.UTF_8))) {
            writer.write(data);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void doGetRequest(HttpGet httpGet) {
        /* 保存返回的 cookies， 单是要 httpClient 复用
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();*/

        /*CloseableHttpClient httpClient = HttpClients.createDefault();*/

        BasicCookieStore cookieStore = new BasicCookieStore();
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
             CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            System.out.println("响应状态 = " + response.getStatusLine().getStatusCode());

            System.out.println("------ request headers ------");
            showAllHeaders(httpGet.getAllHeaders());

            System.out.println("------ response headers ------");
            showAllHeaders(response.getAllHeaders());

            List<Cookie> cookies = cookieStore.getCookies();
            System.out.println("------ cookies start");
            cookies.forEach(cookie -> System.out.println(cookie.getName() + ":" + cookie.getValue()));
            System.out.println("------ cookies end");

            // 解析返回数据
            if (entity != null) {
                Header contentType = entity.getContentType();
                System.out.println("返回内容类型：" + contentType.getName() + " : " + contentType.getValue());

                String entityContent = EntityUtils.toString(entity, "UTF-8");
                System.out.println("entity content : " + entityContent);

                // writeEntityContent(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeEntityContent(HttpEntity entity) {
        InputStream content = null;
        try {
            content = entity.getContent();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(content);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = bufferedInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            System.out.println("解析返回内容: " + new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != content) {
                try {
                    content.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void showAllHeaders(Header[] allHeaders) {
        if (null == allHeaders || allHeaders.length == 0) { return; }
        System.out.println("------ Headers Start ------");
        for (Header header : allHeaders) {
            System.out.println(header.getName() + " : " + header.getValue());
        }
        System.out.println("------ Headers End ------");
    }

}
