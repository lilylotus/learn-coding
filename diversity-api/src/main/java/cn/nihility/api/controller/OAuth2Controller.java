package cn.nihility.api.controller;

import cn.nihility.common.constant.Constant;
import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.common.constant.RequestMethodEnum;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.HttpClientUtils;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.common.util.UnifyResultUtils;
import cn.nihility.common.util.UuidUtils;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OAuth 2.0 说明文档 [https://datatracker.ietf.org/doc/html/rfc6749#section-1]
 * <p>
 * 应用服务请求 OAuth2.0 服务认证：
 * https://github.com/login/oauth/authorize?client_id=eb641b716603c1116dfa&redirect_uri=http://localhost:50060/auth/github/callback
 * <p>
 * OAuth2.0 认证应用成功，携带 code 调用回调接口：
 * http://localhost:50060/login/auth/callback?code=2cce19f0363e7b4e41bc
 * <p>
 * 应用去获取访问 token：
 * POST https://github.com/login/oauth/token?client_id=eb641b716603c1116dfa&client_secret=ab3490c83db2c99b6bc985ef0ab9b4e32d7b5b39&code=2cce19f0363e7b4e41bc&redirect_uri=http://localhost:50060/auth/github/callback
 * <p>
 * 应用依据 access_token 访问 header - Authorization:token 获取用户信息：
 * GET https://api.github.com/user
 */
@Controller
public class OAuth2Controller {

    private static final Logger log = LoggerFactory.getLogger(OAuth2Controller.class);

    private static final String OAUTH2_CODE_GRANT = "code";
    private static final String OAUTH2_IMPLICIT_GRANT = "token";

    private static final String OAUTH2_RESPONSE_TOKEN_TYPE = "Bearer";
    private static final String OAUTH2_RESPONSE_ACCESS_TOKEN_TAG = "access_token";

    private static final String CODE_GRANT_AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE_GRANT_REFRESH_TOKEN = "refresh_token";

    private static final String USER_INFO_URL = "http://127.0.0.1:30010/login/oauth2/user-info";

    /**
     * http://127.0.0.1:30010/login/oauth/authorize?client_id=id1234567890&redirect_uri=http://127.0.0.1:30010/login/auth/callback?ch=中文
     * http://127.0.0.1:30010/login/oauth/authorize?client_id=id1234567890&redirect_uri=http%3A%2F%2F127.0.0.1%3A30010%2Flogin%2Fauth%2Fcallback%3Fch%3D%E4%B8%AD%E6%96%87
     * <p>
     * response_type=code 要求返回授权码（code）/ 隐式授权模式 (token)
     * client_id=CLIENT_ID 让 OAUTH2.0 知道是谁在请求
     * redirect_uri=CALLBACK_URL 接受或拒绝请求后的跳转网址
     * scope=read 要求的授权范围（这里是只读）
     * https://auth.com/oauth/authorize?response_type=code&client_id=CLIENT_ID&scope=read&redirect_uri=CALLBACK_URL
     * <p>
     * application/x-www-form-urlencoded - format
     * code: GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
     * Host: server.example.com
     * code response:
     * HTTP/1.1 302 Found
     * Location: https://client.example.com/cb?code=SplxlOBeZQQYbYS6WxSbIA&state=xyz
     * <p>
     * implicit:
     * GET /authorize?response_type=token&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
     * Host: server.example.com
     * <p>
     * HTTP/1.1 302 Found
     * Location: http://example.com/cb#access_token=2YotnFZFEjr1zCsicMWpAA&state=xyz&token_type=example&expires_in=3600
     * <p>
     * 授权码模式
     * http://127.0.0.1:30010/login/oauth2/authorize?response_type=code&client_id=ClientId1234567890&scope=all&redirect_uri=http%3A%2F%2F127.0.0.1%3A30010%2Flogin%2Foauth2%2Fcallback%2Fcode%3FRedirectParam%3D%E4%B8%AD%E6%96%87
     * 隐式模式
     * http://127.0.0.1:30010/login/oauth2/authorize?response_type=token&client_id=ClientId1234567890&scope=all&redirect_uri=http%3A%2F%2F127.0.0.1%3A30010%2Flogin%2Foauth2%2Fcallback%2Ftoken
     */
    @GetMapping("/login/oauth2/authorize")
    public String loginOauth(@RequestParam("response_type") String responseType,
                             @RequestParam("client_id") String clientId,
                             @RequestParam("scope") String scope,
                             @RequestParam("redirect_uri") String redirectUri,
                             RedirectAttributes redirectAttributes) {

        log.info("OAuth2.0 Authorize response_type=[{}], client_id=[{}], scope=[{}], redirect_uri=[{}]",
            responseType, clientId, scope, redirectUri);

        redirectAttributes.addAttribute("client_id", clientId);
        redirectAttributes.addAttribute("chinese", "中文名称");
        redirectAttributes.addAttribute("redirect_uri", redirectUri);
        // 隐式授权方式
        // 相比授权码授权，隐式授权少了第一步获取 Authorization Code 的过程，因此变得更为简单。但正因为如此也降低了安全性。
        if (OAUTH2_IMPLICIT_GRANT.equals(responseType)) {
            redirectAttributes.addAttribute(OAUTH2_RESPONSE_ACCESS_TOKEN_TAG, UuidUtils.jdkUUID());
            redirectAttributes.addAttribute("token_type", OAUTH2_RESPONSE_TOKEN_TYPE);
            redirectAttributes.addAttribute("expires_in", 3600);
        } else if (OAUTH2_CODE_GRANT.equals(responseType)) {
            // 返回授权码
            redirectAttributes.addAttribute("code", UuidUtils.jdkUUID());
        } else {
            throw new HttpRequestException(HttpStatus.OK,
                UnifyResultUtils.failure("OAUTH2 授权类型不支持 [" + responseType + "]"));
        }

        // 对跳转 url 的参数编码，防止中文乱码
        return "redirect:" + HttpRequestUtils.urlParamsEncode(redirectUri);

    }

    /**
     * grant_type ：授权类型，授权码固定的值为 authorization_code ， 更新令牌 refresh_token ,implicit 隐式认证
     * code：这个就是上一步获取的授权码
     * <p>
     * POST /token HTTP/1.1
     * Host: server.example.com
     * Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
     * Content-Type: application/x-www-form-urlencoded
     * <p>
     * /token?grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
     * <p>
     * {
     * "access_token":"2YotnFZFEjr1zCsicMWpAA",
     * "token_type":"Bearer",
     * "expires_in":3600,
     * "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
     * "scope":"read",
     * "example_parameter":"example_value"
     * }
     * <p>
     * Refresh Token:
     * POST /token HTTP/1.1
     * Host: server.example.com
     * Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
     * Content-Type: application/x-www-form-urlencoded
     * <p>
     * grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
     */
    @PostMapping("/login/oauth2/token")
    @ResponseBody
    public Map<String, Object> oauthAccessToken(@RequestParam("grant_type") String grantType,
                                                @RequestParam("code") String code,
                                                @RequestParam("redirect_uri") String redirectUri,
                                                @RequestParam("client_id") String clientId) {

        log.info("OAuth2.0 token client_id=[{}], grant_type=[{}], redirect_uri=[{}], code=[{}]",
            clientId, grantType, redirectUri, code);

        Map<String, Object> result = new HashMap<>(8);
        result.put(OAUTH2_RESPONSE_ACCESS_TOKEN_TAG, UuidUtils.jdkUUID());

        if (CODE_GRANT_AUTHORIZATION_CODE.equals(grantType)) {
            result.put(CODE_GRANT_REFRESH_TOKEN, UuidUtils.jdkUUID());
        }

        // Bearer
        result.put("token_type", OAUTH2_RESPONSE_TOKEN_TYPE);
        // 1 小时
        result.put("expires_in", 3600);
        result.put("scope", "read");
        result.put("example_parameter", "example_value");

        return result;
    }

    @GetMapping("/login/oauth2/callback/token")
    @ResponseBody
    public UnifyResult<Map<String, String>> authTokenCallback(@RequestParam(OAUTH2_RESPONSE_ACCESS_TOKEN_TAG) String accessToken,
                                                              @RequestParam("expires_in") String expireIn,
                                                              @RequestParam("token_type") String tokenType) {
        log.info("OAuth2.0 token grant call back, access_token [{}], expires_in [{}], token_type [{}]",
            accessToken, expireIn, tokenType);

        // 依据 access token 获取用户信息
        HttpGet get = new HttpGet(USER_INFO_URL);
        HttpRequestUtils.addHeader(get, Constant.AUTHENTICATION_TOKEN_KEY,
            Constant.AUTHENTICATION_BEARER_TOKEN_PREFIX + accessToken);
        @SuppressWarnings("unchecked")
        Map<String, String> userInfoResult = HttpClientUtils.executeHttpRequest(get, Map.class);

        log.info("用户信息 [{}]", userInfoResult);

        return UnifyResultUtils.success(userInfoResult);

    }

    @GetMapping("/login/oauth2/callback/code")
    @ResponseBody
    public UnifyResult<Map<String, String>> authCallback(@RequestParam("code") String code) {

        log.info("callback code [{}]", code);

        // 依据 code 获取 access token
        String url = "http://127.0.0.1:30010/login/oauth2/token";
        Map<String, String> params = new HashMap<>(8);
        params.put("client_id", "OAuth2.0 Client Id");
        params.put("client_secret", "OAuth2.0 Client Secret");
        params.put("redirect_uri", "http://127.0.0.1:30010/login/oauth2/callback?RedirectParam=中文");
        params.put("grant_type", CODE_GRANT_AUTHORIZATION_CODE);
        params.put("code", code);

        @SuppressWarnings("unchecked")
        Map<String, String> accessTokenMap = HttpClientUtils.executeForm(url, RequestMethodEnum.POST, params, Map.class);
        log.info("获取 access_token 响应数据 [{}]", accessTokenMap);
        String accessToken = null;
        if (accessTokenMap == null || accessTokenMap.isEmpty()) {
            log.error("获取 access_token 响应为空");
        } else {
            accessToken = accessTokenMap.get(OAUTH2_RESPONSE_ACCESS_TOKEN_TAG);
        }
        if (StringUtils.isBlank(accessToken)) {
            throw new HttpRequestException(HttpStatus.UNAUTHORIZED, UnifyResultUtils.failure("获取 access_token 失败"));
        }

        // 依据 access token 获取用户信息
        HttpGet get = new HttpGet(USER_INFO_URL);
        HttpRequestUtils.addHeader(get, Constant.AUTHENTICATION_TOKEN_KEY,
            Constant.AUTHENTICATION_BEARER_TOKEN_PREFIX + accessToken);
        @SuppressWarnings("unchecked")
        Map<String, String> userInfoResult = HttpClientUtils.executeHttpRequest(get, Map.class);

        log.info("用户信息 [{}]", userInfoResult);

        return UnifyResultUtils.success(userInfoResult);
    }

    @GetMapping("/login/oauth2/user-info")
    @ResponseBody
    public Map<String, String> user(HttpServletRequest request) {

        String token = request.getHeader(Constant.AUTHENTICATION_TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(Constant.AUTHENTICATION_TOKEN_KEY);
        }
        if (StringUtils.isBlank(token)) {
            token = HttpRequestUtils.obtainCookieValue(Constant.AUTHENTICATION_TOKEN_KEY, request);
        }
        log.info("Authorization [{}]", token);

        if (StringUtils.isBlank(token)) {
            throw new HttpRequestException(HttpStatus.UNAUTHORIZED,
                UnifyResultUtils.failure("Authorization 请求参数不存在"));
        }

        Map<String, String> info = new HashMap<>(4);
        info.put("id", UUID.randomUUID().toString().replace("-", ""));
        info.put("name", "Random User Name");

        return info;
    }

}
