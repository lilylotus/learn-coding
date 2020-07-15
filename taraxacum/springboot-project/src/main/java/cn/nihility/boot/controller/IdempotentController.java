package cn.nihility.boot.controller;

import cn.nihility.boot.annotation.AutoIdempotent;
import cn.nihility.boot.controller.vo.ResultVo;
import cn.nihility.boot.service.TokenServiceImpl;
import cn.nihility.boot.util.ResultVoUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author dandelion
 * @date 2020-05-07 16:35
 */
@RestController
@RequestMapping("/idempotent")
public class IdempotentController {

    private final static Logger log = LoggerFactory.getLogger(IdempotentController.class);
    private TokenServiceImpl tokenService;

    public IdempotentController(TokenServiceImpl tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/token")
    public ResultVo token() {
        final String token = tokenService.createToken();
        log.info("IdempotentController -> create token [{}]", token);
        if (StringUtils.isNotBlank(token)) {
            return ResultVoUtil.success("Success Create Token.", token);
        }
        return ResultVoUtil.success();
    }

    @AutoIdempotent
    @PostMapping("/idempotent")
    public ResultVo idempotent() {
        log.info("IdempotentController -> idempotent()");
        return ResultVoUtil.success("Test Idempotent success.");
    }

}
