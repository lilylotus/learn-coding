package cn.nihility.cloud.openfeign.controller;

import cn.nihility.cloud.openfeign.service.Resilience4jThreadPoolBulkheadService;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Resilience4jThreadPoolBulkheadController {

    private Resilience4jThreadPoolBulkheadService service;

    public Resilience4jThreadPoolBulkheadController(Resilience4jThreadPoolBulkheadService service) {
        this.service = service;
    }

    @GetMapping("/bulkhead/pool/default")
    public UnifyResult defaultThreadPoolBulkhead() {
        try {
            return UnifyResultUtil.success(service.defaultThreadPoolBulkhead().get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UnifyResultUtil.failure("failure defaultThreadPoolBulkhead");
    }

    @GetMapping("/bulkhead/pool/specific")
    public UnifyResult specificThreadPoolBulkhead() {
        try {
            return UnifyResultUtil.success(service.specificThreadPoolBulkhead().get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UnifyResultUtil.failure("failure specificThreadPoolBulkhead");
    }

    @GetMapping("/bulkhead/pool/config")
    public UnifyResult specificConfigThreadPoolBulkhead() {
        try {
            return UnifyResultUtil.success(service.specificConfigThreadPoolBulkhead().get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UnifyResultUtil.failure("failure specificConfigThreadPoolBulkhead");
    }

}
