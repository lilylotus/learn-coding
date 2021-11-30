package cn.nihility.api.controller;

import cn.nihility.api.constant.Constant;
import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.DefaultHttpClientUtil;
import cn.nihility.common.util.RequestUtil;
import cn.nihility.common.util.UnifyResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 应用服务请求 OAuth2.0 服务认证：
 * https://github.com/login/oauth/authorize?client_id=eb641b716603c1116dfa&redirect_uri=http://localhost:50060/auth/github/callback
 *
 * OAuth2.0 认证应用成功，携带 code 调用回调接口：
 * http://localhost:50060/login/auth/callback?code=2cce19f0363e7b4e41bc
 *
 * 应用去获取访问 token：
 * POST https://github.com/login/oauth/access_token?client_id=eb641b716603c1116dfa&client_secret=ab3490c83db2c99b6bc985ef0ab9b4e32d7b5b39&code=2cce19f0363e7b4e41bc&redirect_uri=http://localhost:50060/auth/github/callback
 *
 * 应用依据访问 header - Authorization:token 获取用户信息：
 * GET https://api.github.com/user
 */
@Controller
public class OAuth2Controller {

    private static final Logger log = LoggerFactory.getLogger(OAuth2Controller.class);

    /*
    * http://127.0.0.1:30010/login/oauth/authorize?client_id=id1234567890&redirect_uri=http://127.0.0.1:30010/login/auth/callback?ch=中文
    * http://127.0.0.1:30010/login/oauth/authorize?client_id=id1234567890&redirect_uri=http%3A%2F%2F127.0.0.1%3A30010%2Flogin%2Fauth%2Fcallback%3Fch%3D%E4%B8%AD%E6%96%87
    * */
    @GetMapping("/login/oauth/authorize")
    public String loginOauth(@RequestParam("client_id") String clientId,
                             @RequestParam("redirect_uri") String redirectUri,
                             RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("client_id", clientId);
        redirectAttributes.addAttribute("chinese", "中文名称");
        redirectAttributes.addAttribute("redirect_uri", redirectUri);
        redirectAttributes.addAttribute("code", UUID.randomUUID().toString().replace("-", ""));

        // 对跳转 url 的参数编码，防止中文乱码
        return "redirect:" + RequestUtil.urlParamsEncode(redirectUri);

    }

    @PostMapping("/login/oauth/access_token")
    @ResponseBody
    public Map<String, Object> oauthAccessToken(@RequestParam("client_id") String clientId,
                                                @RequestParam("client_secret") String clientSecret,
                                                @RequestParam("code") String code,
                                                @RequestParam("redirect_uri") String redirectUri) {

        Map<String, Object> result = new HashMap<>();
        result.put("access_token", UUID.randomUUID().toString().replace("-", ""));
        result.put("scope", "");
        result.put("token_type", "bearer");

        return result;
    }

    @GetMapping("/login/auth/callback")
    @ResponseBody
    public UnifyResult authCallback(@RequestParam("code") String code) {

        log.info("callback code [{}]", code);

        // 依据 code 获取 access token
        String url = "http://127.0.0.1:30010/login/oauth/access_token";
        Map<String, String> params = new HashMap<>();
        params.put("client_id", "OAuth2.0 Client Id");
        params.put("client_secret", "OAuth2.0 Client Secret");
        params.put("redirect_uri", "OAuth2.0 Client Redirect URI");
        params.put("code", code);

        URI uri = RequestUtil.buildUri(url, params);
        HttpPost post = new HttpPost(uri);
        RequestUtil.addApplicationJsonHeader(post);
        @SuppressWarnings("unchecked")
        Map<String, String> accessTokenMap = DefaultHttpClientUtil.executeHttpRequest(post, Map.class);
        log.info("获取 access_token 响应数据 [{}]", accessTokenMap);
        String accessToken = null;
        if (accessTokenMap == null || accessTokenMap.isEmpty()) {
            log.error("获取 access_token 响应为空");
        } else {
            accessToken = accessTokenMap.get("access_token");
        }
        if (StringUtils.isBlank(accessToken)) {
            throw new HttpRequestException(HttpStatus.UNAUTHORIZED, UnifyResultUtil.failure("获取 access_token 失败"));
        }

        // 依据 access token 获取用户信息
        String userUrl = "http://127.0.0.1:30010/login/auth/user";
        HttpGet get = new HttpGet(userUrl);
        RequestUtil.addHeader(get, Constant.AUTHENTICATION_TOKEN_KEY, accessToken);
        @SuppressWarnings("unchecked")
        Map<String, String> userInfoResult = DefaultHttpClientUtil.executeHttpRequest(get, Map.class);

        log.info("用户信息 [{}]", userInfoResult);

        return UnifyResultUtil.success(userInfoResult);
    }

    @GetMapping("/login/auth/user")
    @ResponseBody
    public Map<String, String> user(HttpServletRequest request) {

        String token = request.getHeader(Constant.AUTHENTICATION_TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(Constant.AUTHENTICATION_TOKEN_KEY);
        }
        if (StringUtils.isBlank(token)) {
            for (Cookie cookie : request.getCookies()) {
                if (Constant.AUTHENTICATION_TOKEN_KEY.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        log.info("Authorization [{}]", token);

        if (StringUtils.isBlank(token)) {
            throw new HttpRequestException(HttpStatus.UNAUTHORIZED,
                UnifyResultUtil.failure("Authorization 请求参数不存在"));
        }

        Map<String, String> info = new HashMap<>();
        info.put("id", UUID.randomUUID().toString().replace("-", ""));
        info.put("name", "Random User Name");

        return info;
    }


}
