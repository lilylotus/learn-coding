package cn.nihility.unify.util;

import cn.nihility.unify.config.JacksonConfiguration;
import cn.nihility.unify.interceptor.RestTemplateTraceIdInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * RestTemplate 工具类
 */
public class RestTemplateUtil {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateUtil.class);

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    /**
     * Rest Template 发送 GET 请求
     * @param url 请求的 url
     */
    public static String doGet(String url, Class<?> clazz) throws JsonProcessingException {
        REST_TEMPLATE.setInterceptors(Collections.singletonList(new RestTemplateTraceIdInterceptor()));
        ObjectMapper objectMapper = new JacksonConfiguration().getObjectMapper();
        Object resultObject = REST_TEMPLATE.getForObject(url, clazz);
        log.info("Rest Template Do Get URI [{}], Response [{}]", url, resultObject);
        return objectMapper.writeValueAsString(resultObject);
    }


}
