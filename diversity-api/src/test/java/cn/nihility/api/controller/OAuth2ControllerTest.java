package cn.nihility.api.controller;

import cn.nihility.common.constant.RequestMethodEnum;
import cn.nihility.common.pojo.ResponseHolder;
import cn.nihility.common.util.HttpClientUtils;
import cn.nihility.common.util.HttpRequestUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/16 17:58
 */
class OAuth2ControllerTest {

    @Test
    void loginOauth() {
        String url = "http://127.0.0.1:30010/login/oauth2/authorize?response_type=token&client_id=ClientId1234567890&scope=all&redirect_uri=http%3A%2F%2F127.0.0.1%3A30010%2Flogin%2Foauth2%2Fcallback%2Ftoken";

        HttpUriRequest request = HttpClientUtils.buildHttpRequest(url, RequestMethodEnum.GET);
        ResponseHolder<Map> responseHolder = HttpClientUtils.executeRequestWithResponse(request, Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> result = responseHolder.getContent();
        Assertions.assertNull(result);

        Assertions.assertEquals(302, responseHolder.getStatusCode());

        Map<String, String> headers = responseHolder.getHeaders();
        System.out.println(headers);
        Assertions.assertNotNull(headers);

        String location = HttpRequestUtils.parseRedirectUrl(responseHolder);
        System.out.println(location);
        Assertions.assertNotNull(location);

    }

}
