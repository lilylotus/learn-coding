package cn.nihility.learn.annotation;

import java.lang.annotation.*;

/**
 * AutoIdempotent
 *
 * @author dandelion
 * @date 2020-05-07 15:42
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoIdempotent {
}
