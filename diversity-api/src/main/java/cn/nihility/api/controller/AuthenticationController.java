package cn.nihility.api.controller;

import cn.nihility.api.annotation.ApiAuthenticationCheck;
import cn.nihility.api.service.IOauth2Service;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.JwtUtils;
import cn.nihility.common.util.UnifyResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author nihility
 */
@Controller
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private IOauth2Service oauth2Service;

    public AuthenticationController(IOauth2Service oauth2Service) {
        this.oauth2Service = oauth2Service;
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

    /**
     * GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
     */
    @GetMapping("/auth/oauth/authorize")
    public void oauthAuthorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = oauth2Service.authorize(request, response);
        logger.info("OAuth 2.0 /authorize redirect url [{}]", redirectUrl);
        response.sendRedirect(redirectUrl);
    }


}
