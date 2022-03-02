package cn.nihility.demo.post.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author sakura
 * @date 2022-03-02 23:27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ProxyAutoScanConfigurationImpl.class)
public @interface ProxyAutoScan {

    String[] scanPackages() default {};

}
