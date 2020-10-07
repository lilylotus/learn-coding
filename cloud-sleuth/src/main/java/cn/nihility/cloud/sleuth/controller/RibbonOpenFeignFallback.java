package cn.nihility.cloud.sleuth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RibbonOpenFeignFallback implements RibbonOpenFeign {
    @Override
    public ResponseEntity<Object> getEmployeeById(Integer id) {
        return ResponseEntity.badRequest().body("服务降级getEmployeeById");
    }

    @Override
    public ResponseEntity<Object> id() {
        return ResponseEntity.badRequest().body("服务降级id");
    }
}
