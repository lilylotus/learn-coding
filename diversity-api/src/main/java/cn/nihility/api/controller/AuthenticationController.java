package cn.nihility.api.controller;

import cn.nihility.api.annotation.ApiAuthenticationCheck;
import cn.nihility.api.dto.CasResponse;
import cn.nihility.api.dto.Oauth2Response;
import cn.nihility.api.service.IAuthenticationService;
import cn.nihility.api.service.ICasService;
import cn.nihility.api.service.IOauth2Service;
import cn.nihility.common.constant.AuthConstant;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.JwtUtils;
import cn.nihility.common.util.UnifyResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author nihility
 */
@Controller
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private IOauth2Service oauth2Service;
    private IAuthenticationService authenticationService;
    private ICasService casService;

    public AuthenticationController(IOauth2Service oauth2Service,
                                    IAuthenticationService authenticationService,
                                    ICasService casService) {
        this.oauth2Service = oauth2Service;
        this.authenticationService = authenticationService;
        this.casService = casService;
    }

    @PostMapping("/jwt/{id}")
    @ResponseBody
    public UnifyResult<JwtUtils.JwtHolder> jwt(@PathVariable String id) {
        JwtUtils.JwtHolder jwtHolder = JwtUtils.createJwtToken(id, null);
        logger.info("jwt [{}]", jwtHolder);
        return UnifyResultUtils.success(jwtHolder);
    }

    @PostMapping("/jwt/verify")
    @ApiAuthenticationCheck
    @ResponseBody
    public UnifyResult<String> authentication() {
        logger.info("jwt verify");
        return UnifyResultUtils.success("Jwt Verify Success!");
    }

    @GetMapping("/auth/login")
    public String authLogin(HttpServletRequest request, Model model) {
        String redirect = request.getParameter(AuthConstant.REDIRECT_TAG);
        logger.info("GET /auth/login redirect [{}]", redirect);

        model.addAttribute(AuthConstant.REDIRECT_TAG, redirect);

        return "auth-login.html";
    }

    @PostMapping("/auth/login")
    public String authLoginValidate(HttpServletRequest request, HttpServletResponse response) {
        String redirect = authenticationService.auth(request, response);
        logger.info("POST /auth/login redirect [{}]", redirect);
        return "redirect:" + redirect;
    }

    /* ============================== OAuth2.0 ============================== */

    /**
     * GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
     */
    @GetMapping("/auth/oauth/authorize")
    public void oauthAuthorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = oauth2Service.authorize(request, response);
        logger.info("OAuth 2.0 /authorize redirect url [{}]", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    /**
     * POST /token?grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
     */
    @RequestMapping(value = "/auth/oauth/token", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Oauth2Response oauthAuthorizeToken(HttpServletRequest request, HttpServletResponse response) {
        return oauth2Service.createCodeGrantToken(request, response);
    }

    @RequestMapping(value = "/auth/user-info", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> userInfo(HttpServletRequest request, HttpServletResponse response) {
        return authenticationService.userInfo(request, response);
    }

    /* ========== CAS https://apereo.github.io/cas/6.0.x/protocol/CAS-Protocol.html
    * http://127.0.0.1:30010/auth/cas/login?service=http%3A%2F%2F127.0.0.1%3A30010%2Fauth%2Fcas%2Frec%2Fdemo
    * */

    @GetMapping("/auth/cas/login")
    public String casLoginForm(HttpServletRequest request, HttpServletResponse response) {
        String redirectUri = casService.casLogin(request, response);
        logger.info("cas login redirect url [{}]", redirectUri);
        return "redirect:" + redirectUri;
    }

    @GetMapping(value = "/auth/cas/serviceValidate")
    @ResponseBody
    public CasResponse casLoginFormValidate(HttpServletRequest request, HttpServletResponse response) {
        return casService.serviceValidate(request, response);
    }

    @GetMapping("/auth/cas/rec/demo")
    @ResponseBody
    public Map<String, Object> casLoginRecDemo(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resp = casService.rec(request, response);
        logger.info("CAS login response [{}]", resp);
        return resp;
    }

}
