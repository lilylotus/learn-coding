package cn.nihility.api.controller;

import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.common.constant.UnifyCodeMapping;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.DefaultHttpClientUtil;
import cn.nihility.common.util.ServletRequestUtil;
import cn.nihility.common.util.UnifyResultUtil;
import cn.nihility.common.util.UuidUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class CASController {

    private static final Logger logger = LoggerFactory.getLogger(CASController.class);

    //private static final String TGT = "TGT";
    private static final String CAS_TGC = "CASTGC";
    private static final String TICKET = "ticket";
    private static final String ST_PREFIX = "ST-";
    private static final String TGT_PREFIX = "TGT-";
    private static final String COOKIE_JSESSIONID = "JSESSIONID";
    private static final String CAS_DOMAIN = "localhost";
    private static final String CAS_COOKIE_PATH = "/cas";

    private static final Set<String> TGT_SET = new HashSet<>();
    private static final Map<String, String> JSESSIONID_MAPPING_SERVICE = new ConcurrentHashMap<>();

    /**
     * 访问登录接口，重定向到 CAS 服务器进行单点认证
     */
    @GetMapping("/cas/business/login")
    public String casLogin() {

        String service = "http://127.0.0.1:30010/cas/business/callback?chinese=中文&english=English";
        String urlEncodeService = ServletRequestUtil.urlParamsEncode(service);
        String redirectUrl = "http://127.0.0.1:30010/cas/auth/login?service=" + urlEncodeService;
        logger.info("redirect url [{}]", redirectUrl);

        return "redirect:" + redirectUrl;
    }

    /**
     * CAS 认证服务端的回调的业务接口，回传 ticket 在去校验并获取用户信息
     */
    @GetMapping("/cas/business/callback")
    @ResponseBody
    public UnifyResult<Map<String, String>> casLoginCallback(@RequestParam("ticket") String ticket) {
        logger.info("CAS Server Callback Ticket [{}]", ticket);

        // 校验 ticket 获取用户信息
        String url = "http://127.0.0.1:30010/cas/auth/serviceValidate";
        Map<String, String> params = new HashMap<>();
        params.put("service", "http://127.0.0.1:30010/cas/business/callback");
        params.put(TICKET, ticket);

        URI uri = ServletRequestUtil.buildUri(url, params);
        HttpGet request = new HttpGet(uri);
        @SuppressWarnings("unchecked")
        UnifyResult<Map<String, String>> validateResultData = DefaultHttpClientUtil.executeHttpRequest(request, UnifyResult.class);
        logger.info("向 CAS 服务端校验 ticket 响应数据 [{}]", validateResultData);
        if (validateResultData == null) {
            logger.error("向 CAS 服务端校验 ticket 请求失败");
            throw new HttpRequestException(HttpStatus.UNAUTHORIZED, UnifyResultUtil.failure("向 CAS 服务端校验 ticket 请求失败"));
        } else {
            if (UnifyCodeMapping.SUCCESS.getCode().equals(validateResultData.getCode())) {
                return UnifyResultUtil.success(validateResultData.getData());
            } else {
                String message = validateResultData.getMessage();
                logger.error("向 CAS 服务端校验 ticket 异常 [{}]", message);
                throw new HttpRequestException(HttpStatus.UNAUTHORIZED, UnifyResultUtil.failure(message));
            }
        }
    }


    /* ============================== CAS SERVER ============================== */

    private void logRequestCookies(HttpServletRequest request) {
        logger.info("Request Cookies [{}]", ServletRequestUtil.cookiesToMap(request));
    }

    /**
     * CAS 服务登录接口，重定向到登录表单
     */
    @GetMapping("/cas/auth/login")
    public String casServerAuthLogin(@RequestParam("service") String service, HttpServletRequest request) {

        logRequestCookies(request);

        final String tgt = ServletRequestUtil.obtainHttpRequestCookieValue(CAS_TGC, request);
        String jsessionid = ServletRequestUtil.obtainCookieJSESSIONID(request);
        logger.info("CAS Auth Service [{}] TGT [{}] JSESSIONID [{}]", service, tgt, jsessionid);
        if (null != jsessionid) {
            JSESSIONID_MAPPING_SERVICE.put(jsessionid, service);
        }

        if (StringUtils.isNotBlank(tgt) || TGT_SET.contains(tgt)) {
            String uuid = ST_PREFIX + UuidUtil.jdkUUID();
            String url = ServletRequestUtil.urlParamsEncode(ServletRequestUtil.addUrlParam(service, TICKET, uuid));
            logger.info("TGT [{}] 存在，直接返回业务地址 [{}] ST [{}]", tgt, url, uuid);
            return "redirect:" + url;
        }

        logger.info("TGT [{}] 不存在，前往登录认证界面", tgt);
        return "login";

    }

    @PostMapping("/cas/auth/password/login")
    public String passwordLoginValidate(String username, String password,
                                        HttpServletRequest request, HttpServletResponse response,
                                        RedirectAttributes redirectAttributes) {
        logRequestCookies(request);
        String jsessionid = ServletRequestUtil.obtainCookieJSESSIONID(request);
        logger.info("CAS Auth Password Validate Form Data [{}]:[{}] , JSESSIONID [{}]", username, password, jsessionid);

        String service = null;
        if (null != jsessionid) {
            service = JSESSIONID_MAPPING_SERVICE.get(jsessionid);
        }
        if (null == service) {
            service = "http://localhost:30030/cas/business/callback?chinese=中文&english=English";
        }

        String tgt = TGT_PREFIX + UuidUtil.jdkUUID();
        String st = ST_PREFIX + UuidUtil.jdkUUID();
        TGT_SET.add(tgt);
        logger.info("CAS TGC = [{}]", tgt);
        ServletRequestUtil.addCookie(CAS_TGC, tgt, null, CAS_COOKIE_PATH, 3600, response);

        /*redirectAttributes.addAttribute(TICKET, uuid);
        String serviceUrl = RequestUtil.urlParamsEncode(service);*/

        String serviceUrl = ServletRequestUtil.urlParamsEncode(ServletRequestUtil.addUrlParam(service, TICKET, st));
        logger.info("CAS Auth Redirect Service [{}]", serviceUrl);

        return "redirect:" + serviceUrl;
    }

    /**
     * CAS 服务端校验 ticket (ST)
     * /cas/auth/serviceValidate?service={业务系统地址}&ticket={ticket}
     */
    @GetMapping("/cas/auth/serviceValidate")
    @ResponseBody
    public UnifyResult<Map<String, String>> casServerServiceValidate(@RequestParam("service") String service,
                                                        @RequestParam("ticket") String ticket,
                                                        HttpServletRequest request) {
        logger.info("CAS Auth Service Validate [{}]:[{}]", service, ticket);
        logRequestCookies(request);
        if (StringUtils.isBlank(ticket)) {
            logger.error("Ticket 校验票据不存在");
            throw new HttpRequestException(HttpStatus.UNAUTHORIZED, UnifyResultUtil.failure("Ticket 校验票据不存在"));
        }

        Map<String, String> info = new HashMap<>();
        info.put("id", UUID.randomUUID().toString().replace("-", ""));
        info.put("name", "Random User Name");

        return UnifyResultUtil.success(info);
    }

}
