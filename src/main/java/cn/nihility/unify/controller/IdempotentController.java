package cn.nihility.unify.controller;

import cn.nihility.unify.annotaion.ApiIdempotent;
import cn.nihility.unify.annotaion.SkipAuthentication;
import cn.nihility.unify.idempotent.IdempotentService;
import cn.nihility.unify.pojo.UnifyResult;
import cn.nihility.unify.pojo.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 幂等性 controller
 * @author daffodil
 * @date 2020-10-23 23:23:05
 */
@RestController
@RequestMapping("/v1/idempotent")
public class IdempotentController {

    private static final Logger log = LoggerFactory.getLogger(IdempotentController.class);

    private final IdempotentService idempotentService;

    /* 模拟并发处理的数据 */
    private static volatile int count = 100;

    public IdempotentController(IdempotentService idempotentService) {
        this.idempotentService = idempotentService;
    }

    /**
     * 在执行需要幂等性需求的接口前，需要先获取执行接口的唯一 token
     */
    @RequestMapping("/token")
    @SkipAuthentication
    public UnifyResult token() {
        return UnifyResultUtil.success(idempotentService.generateToken());
    }

    @PutMapping("/services")
    @ApiIdempotent
    public UnifyResult services(@RequestBody Map<String, String> data) {
        log.info("update data [{}]", data);
        count--;
        log.info("subtract count by 1, remain [{}]", count);
        return UnifyResultUtil.success(count);
    }


}
