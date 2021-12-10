package cn.nihility.profile.controller;

import cn.nihility.common.constant.UnifyCodeMapping;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.DefaultHttpClientUtil;
import cn.nihility.common.util.RequestUtil;
import cn.nihility.common.util.UnifyResultUtil;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CASClientController {

    private static final Logger logger = LoggerFactory.getLogger(CASClientController.class);

    private static final String SERVICE_URL = "http://localhost:30030/cas/business/callback?chinese=中文&english=English";
    private static final String CAS_VALIDATE_URL = "http://localhost:30010/cas/auth/serviceValidate";

    private void logRequestCookies(HttpServletRequest request) {
        logger.info("Request Cookies [{}]", RequestUtil.cookiesToMap(request));
    }

    /**
     * 访问登录接口，重定向到 CAS 服务器进行单点认证
     */
    @GetMapping("/cas/business/login")
    public String casLogin(HttpServletRequest request, HttpServletResponse response) {

        logRequestCookies(request);

        String urlEncodeService = RequestUtil.urlParamsEncode(SERVICE_URL);
        String redirectUrl = "http://localhost:30010/cas/auth/login?service=" + urlEncodeService;
        logger.info("redirect url [{}]", redirectUrl);

        return "redirect:" + redirectUrl;
    }

    /**
     * CAS 认证服务端的回调的业务接口，回传 ticket 在去校验并获取用户信息
     */
    @GetMapping("/cas/business/callback")
    @ResponseBody
    public UnifyResult casLoginCallback(@RequestParam("ticket") String ticket, HttpServletRequest servletRequest) {
        logger.info("CAS Server Callback Ticket [{}]", ticket);
        logRequestCookies(servletRequest);
        // 校验 ticket 获取用户信息
        Map<String, String> params = new HashMap<>();
        params.put("service", SERVICE_URL);
        params.put("ticket", ticket);

        URI uri = RequestUtil.buildUri(CAS_VALIDATE_URL, params);
        HttpGet request = new HttpGet(uri);
        UnifyResult validateResultData = DefaultHttpClientUtil.executeHttpRequest(request, UnifyResult.class);
        logger.info("向 CAS 服务端校验 ticket 响应数据 [{}]", validateResultData);
        if (validateResultData == null) {
            logger.error("向 CAS 服务端校验 ticket 请求失败");
            return UnifyResultUtil.success("向 CAS 服务端校验 ticket 请求失败");
        } else {
            if (UnifyCodeMapping.SUCCESS.getCode().equals(validateResultData.getCode())) {
                return UnifyResultUtil.success(validateResultData.getData());
            } else {
                String message = validateResultData.getMessage();
                logger.error("向 CAS 服务端校验 ticket 异常 [{}]", message);
                return UnifyResultUtil.failure(message);
            }
        }
    }

}
