package cn.nihility.api.controller;

import cn.nihility.api.annotation.ApiAuthenticationCheck;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.JwtUtil;
import cn.nihility.common.util.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/jwt/{id}")
    public UnifyResult<String> jwt(@PathVariable String id) {
        String token = JwtUtil.createJwtToken(id, null);
        logger.info("jwt [{}]", token);
        return UnifyResultUtil.success(token);
    }

    @PostMapping("/jwt/verify")
    @ApiAuthenticationCheck
    public UnifyResult<String> authentication() {
        logger.info("jwt verify");
        return UnifyResultUtil.success("Jwt Verify Success!");
    }

}
