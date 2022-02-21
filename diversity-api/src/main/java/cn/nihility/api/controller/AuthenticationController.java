package cn.nihility.api.controller;

import cn.nihility.api.annotation.ApiAuthenticationCheck;
import cn.nihility.api.dto.Oauth2Response;
import cn.nihility.api.service.IAuthenticationService;
import cn.nihility.api.service.IOauth2Service;
import cn.nihility.api.service.ISessionService;
import cn.nihility.api.service.ITokenService;
import cn.nihility.common.constant.AuthConstant;
import cn.nihility.common.entity.AuthenticateSession;
import cn.nihility.common.entity.AuthenticationToken;
import cn.nihility.common.exception.AuthenticationException;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.common.util.JwtUtils;
import cn.nihility.common.util.UnifyResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author nihility
 */
@Controller
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private IOauth2Service oauth2Service;
    private IAuthenticationService authenticationService;
    private ISessionService sessionService;
    private ITokenService tokenService;

    public AuthenticationController(IOauth2Service oauth2Service,
                                    IAuthenticationService authenticationService,
                                    ISessionService sessionService,
                                    ITokenService tokenService) {
        this.oauth2Service = oauth2Service;
        this.authenticationService = authenticationService;
        this.sessionService = sessionService;
        this.tokenService = tokenService;
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

    /* ============================== OAuth2.0 ============================== */

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
    @PostMapping("/auth/oauth/token")
    @ResponseBody
    public Oauth2Response oauthAuthorizeToken(HttpServletRequest request, HttpServletResponse response) {
        return oauth2Service.createCodeGrantToken(request, response);
    }

    @GetMapping("/auth/user-info")
    @ResponseBody
    public Map<String, Object> userInfo(HttpServletRequest request, HttpServletResponse response) {
        String token = HttpRequestUtils.bearerTokenValue(request.getHeader(AuthConstant.AUTHORIZATION_KEY));
        logger.info("token [{}]", token);
        AuthenticationToken aToken = tokenService.getTokenById(token);
        if (StringUtils.isBlank(token) || null == aToken) {
            logger.error("请求 token 为空");
            throw new AuthenticationException("请求 token 不可为空");
        }
        AuthenticateSession authSession = sessionService.getSessionById(aToken.getSessionId(), response);
        if (authSession == null) {
            logger.error("用户未认证，认证 session 不存在");
            throw new AuthenticationException("用户未认证，认证 session 不存在");
        }

        return Optional.ofNullable(authSession.getUserAttributes()).orElse(Collections.emptyMap());

    }


}
