package cn.nihility.cloud.openfeign.proxy.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RemoteFeignInvoke {
}
