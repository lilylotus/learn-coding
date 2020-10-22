package cn.nihility.unify.controller;

import cn.nihility.unify.pojo.UnifyResult;
import cn.nihility.unify.pojo.UnifyResultUtil;
import cn.nihility.unify.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 登录控制器
 */
@RestController
@RequestMapping("/v1/login")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @PutMapping()
    public UnifyResult login(@RequestBody Map<String, String> info) {
        String token = JWTUtil.generateJwtToken(info);
        if (log.isDebugEnabled()) {
            log.debug("Generate token [{}]", token);
        }
        return UnifyResultUtil.success(token, "登录成功");
    }

}
