package cn.nihility.api.controller;

import cn.nihility.api.annotation.ApiAuthenticationCheck;
import cn.nihility.api.dto.CasResponse;
import cn.nihility.api.dto.Oauth2Response;
import cn.nihility.api.dto.OidcTokenDto;
import cn.nihility.api.service.IAuthenticationService;
import cn.nihility.api.service.ICasService;
import cn.nihility.api.service.IOauth2Service;
import cn.nihility.api.service.IOpenidService;
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

    private final IOauth2Service oauth2Service;
    private final IOpenidService openidService;
    private final IAuthenticationService authenticationService;
    private final ICasService casService;

    public AuthenticationController(IOauth2Service oauth2Service,
                                    IOpenidService openidService,
                                    IAuthenticationService authenticationService,
                                    ICasService casService) {
        this.oauth2Service = oauth2Service;
        this.openidService = openidService;
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

    /* ============================== openid https://openid.net/specs/openid-connect-core-1_0.html */

    /*
        The Authorization Code Flow goes through the following steps.
            Client prepares an Authentication Request containing the desired request parameters.
            Client sends the request to the Authorization Server.
            Authorization Server Authenticates the End-User.
            Authorization Server obtains End-User Consent/Authorization.
            Authorization Server sends the End-User back to the Client with an Authorization Code.
            Client requests a response using the Authorization Code at the Token Endpoint.
            Client receives a response that contains an ID Token and Access Token in the response body.
            Client validates the ID token and retrieves the End-User's Subject Identifier.
    * */

    /**
    * GET /authorize?
        response_type=code
        &scope=openid%20profile%20email
        &client_id=s6BhdRkqt3
        &state=af0ifjsldkj
        &redirect_uri=https%3A%2F%2Fclient.example.org%2Fcb HTTP/1.1

    * http://127.0.0.1:30010/auth/oidc/authorize?response_type=code&client_id=s6BhdRkqt3&scope=openid&state=xyz&redirect_uri=http%3A%2F%2F127.0.0.1%3A30010%2Fauth%2Foidc%2Fcallback%3Fchinese%3D%E4%B8%AD%E6%96%87%26english%3DEnglish
    * http://127.0.0.1:30010/auth/oidc/authorize?response_type=token&client_id=s6BhdRkqt3&scope=openid&state=xyz&redirect_uri=http%3A%2F%2F127.0.0.1%3A30010%2Fwelcome%3Fchinese%3D%E4%B8%AD%E6%96%87%26english%3Denglish
    */
    @GetMapping("/auth/oidc/authorize")
    public void oidcAuthorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = openidService.authorize(request, response);
        logger.info("OIDC /authorize redirect url [{}]", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    /**
     * POST /token HTTP/1.1
     * Content-Type: application/x-www-form-urlencoded
     *
     * /token?client_id=yzx&grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&redirect_uri=http%3A%2F%2F127.0.0.1%3A30010%2Fauth%2Foidc%2Fcallback%3Fchinese%3D%E4%B8%AD%E6%96%87%26english%3DEnglish
     *
     */
    @PostMapping("/auth/oidc/token")
    @ResponseBody
    public OidcTokenDto oidcToken(HttpServletRequest request, HttpServletResponse response) {
        return openidService.codeConvertToken(request, response);
    }

    @RequestMapping(value = "/auth/oidc/user-info", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> oidcUserInfo(HttpServletRequest request, HttpServletResponse response) {
        return openidService.userInfo(request, response);
    }

    @RequestMapping(value = "/auth/oidc/callback", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> oidcRecDemo(HttpServletRequest request, HttpServletResponse response) {
        return openidService.callback(request, response);
    }


    /* ============================== OAuth2.0 https://datatracker.ietf.org/doc/html/rfc6749#section-4.1 */

    /**
     * GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
     *
     * scope - OPTIONAL
     *
     * HTTP/1.1 302 Found
     * Location: https://client.example.com/cb?code=SplxlOBeZQQYbYS6WxSbIA&state=xyz
     *
     * example:
     * http://127.0.0.1:30010/auth/oauth/authorize?response_type=code&client_id=s6BhdRkqt3&scope=all&state=xyz&redirect_uri=http%3A%2F%2F127.0.0.1%3A30010%2Fauth%2Foauth%2Frec%2Fdemo%3Fparam1%3D%E4%B8%AD%E6%96%87%26param2%3DEnglish
     */
    @GetMapping("/auth/oauth/authorize")
    public void oauthAuthorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = oauth2Service.authorize(request, response);
        logger.info("OAuth 2.0 /authorize redirect url [{}]", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    /**
     * POST /token HTTP/1.1
     * Host: server.example.com
     * Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
     * Content-Type: application/x-www-form-urlencoded
     *
     * scope - OPTIONAL
     *
     * grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
     *
     * response:
     *  {
     *    "access_token":"2YotnFZFEjr1zCsicMWpAA",
     *    "token_type":"example",
     *    "expires_in":3600,
     *    "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
     *    "example_parameter":"example_value"
     *  }
     *
     *  refresh token:
     *  grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
     *
     *  http://127.0.0.1:30010/auth/oauth/token?grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&redirect_uri=http%3A%2F%2F127.0.0.1%3A30010%2Fauth%2Foauth%2Frec%2Fdemo%3Fparam1%3D%E4%B8%AD%E6%96%87%26param2%3DEnglish
     *  http://127.0.0.1:30010/auth/oauth/token?grant_type=refresh_token&refresh_token=RT-4cf13ce4eec843169e113f10f3c2c3d9
     */
    @RequestMapping(value = "/auth/oauth/token", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Oauth2Response oauthAuthorizeToken(HttpServletRequest request, HttpServletResponse response) {
        return oauth2Service.createCodeGrantToken(request, response);
    }

    @RequestMapping(value = "/auth/oauth/user-info", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> userInfo(HttpServletRequest request, HttpServletResponse response) {
        return oauth2Service.userInfo(request, response);
    }

    @RequestMapping(value = "/auth/oauth/callback", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> oauthRecDemo(HttpServletRequest request, HttpServletResponse response) {
        return oauth2Service.callback(request, response);
    }

    /* ========== CAS https://apereo.github.io/cas/6.0.x/protocol/CAS-Protocol.html */

    /**
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
