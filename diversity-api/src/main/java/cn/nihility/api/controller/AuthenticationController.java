package cn.nihility.api.controller;

import cn.nihility.api.annotation.ApiAuthenticationCheck;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.JwtUtils;
import cn.nihility.common.util.UnifyResultUtils;
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
        String token = JwtUtils.createJwtToken(id, null);
        logger.info("jwt [{}]", token);
        return UnifyResultUtils.success(token);
    }

    @PostMapping("/jwt/verify")
    @ApiAuthenticationCheck
    public UnifyResult<String> authentication() {
        logger.info("jwt verify");
        return UnifyResultUtils.success("Jwt Verify Success!");
    }

}
