package cn.nihility.cloud.openfeign.controller;

import cn.nihility.cloud.openfeign.service.CircuitBreakerServiceImpl;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CircuitBreakerController {

    private CircuitBreakerServiceImpl circuitBreakerService;

    public CircuitBreakerController(CircuitBreakerServiceImpl circuitBreakerService) {
        this.circuitBreakerService = circuitBreakerService;
    }

    @GetMapping("/breaker/noaop")
    public UnifyResult circuitBreakerNoAop() {
        String result = circuitBreakerService.circuitBreakerNoAOPWithDescend();
        return UnifyResultUtil.success(result);
    }

    @GetMapping("/breaker/aop")
    public UnifyResult circuitBreakerAop() {
        String result = circuitBreakerService.circuitBreakerAOP();
        return UnifyResultUtil.success(result);
    }

}
