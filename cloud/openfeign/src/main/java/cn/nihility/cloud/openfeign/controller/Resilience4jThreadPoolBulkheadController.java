package cn.nihility.cloud.openfeign.controller;

import cn.nihility.cloud.openfeign.service.Resilience4jThreadPoolBulkheadService;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Resilience4jThreadPoolBulkheadController {

    private static final Logger log = LoggerFactory.getLogger(Resilience4jThreadPoolBulkheadController.class);

    private Resilience4jThreadPoolBulkheadService service;

    public Resilience4jThreadPoolBulkheadController(Resilience4jThreadPoolBulkheadService service) {
        this.service = service;
    }

    @GetMapping("/bulkhead/pool/default")
    public UnifyResult<String> defaultThreadPoolBulkhead() {
        try {
            return UnifyResultUtil.success(service.defaultThreadPoolBulkhead().get());
        } catch (Exception e) {
            log.error("/bulkhead/pool/default", e);
        }
        return UnifyResultUtil.success("failure defaultThreadPoolBulkhead");
    }

    @GetMapping("/bulkhead/pool/specific")
    public UnifyResult<String> specificThreadPoolBulkhead() {
        try {
            return UnifyResultUtil.success(service.specificThreadPoolBulkhead().get());
        } catch (Exception e) {
            log.error("/bulkhead/pool/specific", e);
        }
        return UnifyResultUtil.success("failure specificThreadPoolBulkhead");
    }

    @GetMapping("/bulkhead/pool/config")
    public UnifyResult<String> specificConfigThreadPoolBulkhead() {
        try {
            return UnifyResultUtil.success(service.specificConfigThreadPoolBulkhead().get());
        } catch (Exception e) {
            log.error("/bulkhead/pool/config", e);
        }
        return UnifyResultUtil.success("failure specificConfigThreadPoolBulkhead");
    }

}
