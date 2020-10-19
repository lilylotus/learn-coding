package cn.nihility.unify.util;

import cn.nihility.unify.pojo.UnifyResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

public class WebUtilTest {

    private static final String GET_URL = "http://127.0.0.1:49000/v1/hei/entity";
    private static final String SUCCESS_GET_URL = "http://127.0.0.1:49000/v1/hei/success";

    @Test
    public void testHttpClientUtil() {
        String response = HttpClientUtil.doGet(GET_URL);
        System.out.println(response);
    }

    @Test
    public void testRestTemplateUtil() throws JsonProcessingException {
        String response = RestTemplateUtil.doGet(SUCCESS_GET_URL, UnifyResult.class);
        System.out.println(response);
    }

}
