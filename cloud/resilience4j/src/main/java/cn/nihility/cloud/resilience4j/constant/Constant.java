package cn.nihility.cloud.resilience4j.constant;

public class Constant {

    private Constant() {
    }

    public static final String CIRCUIT_BREAKER_A = "CircuitBreakerA";
    public static final String CIRCUIT_BREAKER_B = "CircuitBreakerB";

    public static final String TIME_LIMITER_A = "TimeLimiterA";

    public static final String RETRY_A = "RetryA";
    public static final String RETRY_B = "RetryB";

    public static final String BULKHEAD_A = "BulkheadA";
    public static final String BULKHEAD_B = "BulkheadB";

    public static final String RATELIMITER_A = "RatelimiterA";
    public static final String RATELIMITER_B = "RatelimiterB";

}
