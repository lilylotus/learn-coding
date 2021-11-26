package cn.nihility.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 应用服务请求 OAuth2.0 服务认证：
 * https://github.com/login/oauth/authorize?client_id=eb641b716603c1116dfa&redirect_uri=http://localhost:50060/auth/github/callback
 *
 * OAuth2.0 认证应用成功，携带 code 调用回调接口：
 * http://localhost:50060/auth/github/callback?code=2cce19f0363e7b4e41bc
 *
 * 应用去获取该用户信息：
 * POST https://github.com/login/oauth/access_token?client_id=eb641b716603c1116dfa&client_secret=ab3490c83db2c99b6bc985ef0ab9b4e32d7b5b39&code=2cce19f0363e7b4e41bc&redirect_uri=http://localhost:50060/auth/github/callback
 */
@Controller
public class OAuth2Controller {

    private static final Logger log = LoggerFactory.getLogger(OAuth2Controller.class);

    @GetMapping("/login/oauth/authorize")
    public String loginOauth(@RequestParam("client_id") String clientId,
                             @RequestParam("redirect_uri") String redirectUri,
                             RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("client_id", clientId);
        redirectAttributes.addAttribute("chinese", "中文名称");
        redirectAttributes.addAttribute("redirect_uri", redirectUri);
        redirectAttributes.addAttribute("code", UUID.randomUUID().toString().replace("-", ""));

        return "redirect:" + redirectUri;

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

    @GetMapping("/auth/github/callback")
    @ResponseBody
    public Map<String, Object> authCallback(@RequestParam("code") String code) {

        log.info("callback code [{}]", code);
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", UUID.randomUUID().toString().replace("-", ""));
        result.put("scope", "");
        result.put("code", code);
        result.put("token_type", "bearer");

        return result;
    }


}
