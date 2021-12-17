package cn.nihility.cloud.resilience4j.predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class RetryOnExceptionPredicate implements Predicate<Throwable> {

    private static final Logger logger = LoggerFactory.getLogger(RetryOnExceptionPredicate.class);

    @Override
    public boolean test(Throwable throwable) {
        logger.error("Retry 处理异常 [{}]:[{}]", throwable.getClass().getName(), throwable.getLocalizedMessage());
        return true;
    }
}
