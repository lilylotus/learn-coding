package cn.nihility.cloud.resilience4j.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class CircuitBreakerServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerServiceImplTest.class);

    @Autowired
    private CircuitBreakerServiceImpl circuitBreakerService;

    @Test
    void circuitBreakerThreadTest() throws InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 15; i++) {
            pool.submit(circuitBreakerService::circuitBreakerNoAOPWithDescend);
        }
        pool.shutdown();

        while (!pool.isTerminated()) {
            // nothing
        }

        Thread.sleep(10000L);

        log.info("==================== 熔断器状态已转为半开 ====================");

        pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 15; i++) {
            pool.submit(circuitBreakerService::circuitBreakerNoAOPWithDescend);
        }
        pool.shutdown();

        while (!pool.isTerminated()) {
            // nothing
        }

    }

}
