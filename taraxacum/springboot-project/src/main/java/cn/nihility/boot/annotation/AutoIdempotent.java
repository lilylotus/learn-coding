package cn.nihility.boot.annotation;

import java.lang.annotation.*;

/**
 * @author dandelion
 * @date 2020:06:27 20:04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoIdempotent {
}
