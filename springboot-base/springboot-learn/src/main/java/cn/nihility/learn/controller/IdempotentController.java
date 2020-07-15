package cn.nihility.learn.controller;

import cn.nihility.learn.annotation.AutoIdempotent;
import cn.nihility.learn.common.ResultVo;
import cn.nihility.learn.common.ServerResponse;
import cn.nihility.learn.idempotent.TokenServiceImpl;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * IdempotentController
 *
 * @author dandelion
 * @date 2020-05-07 16:35
 */
@Slf4j
@RestController
@RequestMapping("/idempotent")
public class IdempotentController {

    @Autowired
    private TokenServiceImpl tokenService;


    @GetMapping("/token")
    public ResultVo token() {
        final String token = tokenService.createToken();
        log.info("IdempotentController -> token() token [{}]", token);
        if (StringUtils.isNotBlank(token)) {
            ResultVo resultVo = new ResultVo();
            resultVo.setCode(200);
            resultVo.setMessage("Success Create Token.");
            resultVo.setData(token);
            return resultVo;
        }
        return ResultVo.EMPTY;
    }

    @AutoIdempotent
    @PostMapping("/test")
    public String idempotent() {
        log.info("IdempotentController -> idempotent()");
        ServerResponse response = ServerResponse.success("Test Idempotent success");
        return JSON.toJSONString(response);
    }


}
