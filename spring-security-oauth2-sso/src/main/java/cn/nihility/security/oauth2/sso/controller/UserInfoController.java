package cn.nihility.security.oauth2.sso.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author orchid
 * @date 2021-05-13 00:54:19
 */
@RestController
@RequestMapping("/user")
public class UserInfoController {

    private static final Logger log = LoggerFactory.getLogger(UserInfoController.class);

    @RequestMapping("/getCurrentUser")
    public Authentication getCurrentUser(Authentication authentication, HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        log.info("Access token [{}]", authorization);

        return authentication;
    }

}
