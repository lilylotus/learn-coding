package cn.nihility.cloud.openfeign.service;

import cn.nihility.cloud.openfeign.exception.CircuitBreakerExceptionA;

import java.util.function.Predicate;

public class RecordFailurePredicate implements Predicate<Throwable> {

    @Override
    public boolean test(Throwable throwable) {
        return !(throwable.getCause() instanceof CircuitBreakerExceptionA);
    }

}
