package cn.nihility.cloud.resilience4j.service;

import cn.nihility.cloud.resilience4j.exception.CircuitBreakerExceptionA;

import java.util.function.Predicate;

public class RecordFailurePredicate implements Predicate<Throwable> {

    @Override
    public boolean test(Throwable throwable) {
        return !(throwable.getCause() instanceof CircuitBreakerExceptionA);
    }

}
