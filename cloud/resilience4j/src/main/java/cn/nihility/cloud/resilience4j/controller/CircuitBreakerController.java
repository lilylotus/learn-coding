package cn.nihility.cloud.resilience4j.controller;

import cn.nihility.cloud.resilience4j.service.CircuitBreakerServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CircuitBreakerController {

    private CircuitBreakerServiceImpl circuitBreakerService;

    public CircuitBreakerController(CircuitBreakerServiceImpl circuitBreakerService) {
        this.circuitBreakerService = circuitBreakerService;
    }

    @GetMapping("/breaker")
    public ResponseEntity<String> circuitBreakerNoAop() {
        return circuitBreakerService.circuitBreakerNoAOPWithDescend();
    }

    @GetMapping("/breaker/aop")
    public ResponseEntity<String> circuitBreakerAop() {
        return circuitBreakerService.circuitBreakerAOP();
    }

    @GetMapping("/breaker/timelimiter")
    public ResponseEntity<String> circuitBreakerWithTimeLimiter() {
        return circuitBreakerService.circuitBreakerWithTimeLimiter();
    }

    @GetMapping("/breaker/retry")
    public ResponseEntity<String> circuitBreakerWithRetry() {
        return circuitBreakerService.circuitBreakerWithRetry();
    }

    @GetMapping("/breaker/retry/aop")
    public ResponseEntity<String> circuitBreakerWithRetryAOP() {
        return circuitBreakerService.circuitBreakerWithRetryAOP();
    }

    @GetMapping("/breaker/bulkhead/aop")
    public ResponseEntity<String> bulkheadAop() {
        return circuitBreakerService.bulkheadAop();
    }

}
