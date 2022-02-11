package cn.nihility.cloud.openfeign.controller;

import cn.nihility.cloud.openfeign.service.Resilience4jBulkheadService;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Resilience4jBulkheadController {

    private Resilience4jBulkheadService bulkheadService;

    public Resilience4jBulkheadController(Resilience4jBulkheadService bulkheadService) {
        this.bulkheadService = bulkheadService;
    }

    @GetMapping("/bulkhead/default")
    public UnifyResult<String> defaultBulkhead() {
        return UnifyResultUtil.success(bulkheadService.defaultBulkhead());
    }

    @GetMapping("/bulkhead/specific/{id}")
    public UnifyResult<String> specificBulkhead(@PathVariable("id") String id) {
        return UnifyResultUtil.success(bulkheadService.specificBulkhead(id));
    }

    @GetMapping("/bulkhead/config")
    public UnifyResult<String> specificConfigBulkhead() {
        return UnifyResultUtil.success(bulkheadService.specificConfigBulkhead());
    }

}
