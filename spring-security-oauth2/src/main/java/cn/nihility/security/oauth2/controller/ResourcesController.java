package cn.nihility.security.oauth2.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author orchid
 * @date 2021-05-11 01:05:06
 */
@RestController
@RequestMapping("/user")
public class ResourcesController {

    private static final Logger log = LoggerFactory.getLogger(ResourcesController.class);

    @RequestMapping("/getCurrentUser")
    public Authentication getCurrentUser(Authentication authentication, HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        log.info("Access token [{}]", authorization);

        return authentication;
    }


    @RequestMapping("/getCurrentUserJwt")
    public Map<String, Object> getCurrentUserJwt(Authentication authentication, HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        String token = authorization.substring(authorization.indexOf("Bearer ") + 1);
        log.info("Access token [{}]", token);

        Map<String, Object> ret = new HashMap<>();
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey("jwtAccessTokenKey")
            .parseClaimsJws(token);

        ret.put("getPrincipal", authentication.getPrincipal());
        ret.put("data", claimsJws.getBody());

        return ret;
    }
}
