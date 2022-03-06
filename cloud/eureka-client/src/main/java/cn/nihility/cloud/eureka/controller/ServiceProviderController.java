package cn.nihility.cloud.eureka.controller;

import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.common.util.JacksonUtils;
import cn.nihility.common.util.UnifyResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class ServiceProviderController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderController.class);

    @RequestMapping("/service/echo/{msg}")
    public UnifyResult<String> echo(@PathVariable String msg, HttpServletRequest request) {
        String feignHeadValue = HttpRequestUtils.obtainHeaderValue("Feign-Attach", request);
        logger.info("Echo [{}], feignHeadValue [{}]", msg, feignHeadValue);
        return UnifyResultUtils.success(msg);
    }

    @RequestMapping("/service/random/timeout")
    public UnifyResult<String> randomTimeOut() {
        Random random = new Random(System.currentTimeMillis());
        int duration = random.nextInt(10);
        logger.info("randomTimeOut [{}] s", duration);
        try {
            Thread.sleep(duration * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return UnifyResultUtils.success("randomTimeOut [" + duration + "]");
    }

    @GetMapping("/service/all-info")
    public UnifyResult<Map<String, Object>> allRequestInfo(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(8);
        Map<String, String> headers = HttpRequestUtils.requestHeadersToMap(request);
        Map<String, String> cookies = HttpRequestUtils.cookiesToMap(request);
        Map<String, String> params = HttpRequestUtils.paramsToMap(request);

        result.put("headers", headers);
        result.put("cookies", cookies);
        result.put("params", params);

        logger.info("Request all info [{}]", JacksonUtils.toJsonString(result));

        return UnifyResultUtils.success(result);
    }

}
