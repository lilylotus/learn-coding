package cn.nihility.api.controller;

import cn.nihility.api.annotation.ApiIdempotent;
import cn.nihility.api.service.IdempotentService;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 幂等校验对外接口
 */
@RestController
public class IdempotentController {

    private IdempotentService idempotentService;

    public IdempotentController(IdempotentService idempotentService) {
        this.idempotentService = idempotentService;
    }

    @GetMapping("/idempotent/token")
    public UnifyResult<String> token() {
        return UnifyResultUtil.success(idempotentService.generateToken());
    }

    @PostMapping("/idempotent/do")
    @ApiIdempotent
    public UnifyResult<String> doSomethings() {
        return UnifyResultUtil.success("Done");
    }

}
