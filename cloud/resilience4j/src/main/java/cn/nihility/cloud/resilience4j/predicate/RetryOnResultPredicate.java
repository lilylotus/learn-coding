package cn.nihility.cloud.resilience4j.predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Predicate;

public class RetryOnResultPredicate implements Predicate<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RetryOnResultPredicate.class);

    @Override
    public boolean test(Object o) {
        logger.info("Retry 校验返回结果 [{}]", o);
        return Objects.isNull(o);
    }

}
