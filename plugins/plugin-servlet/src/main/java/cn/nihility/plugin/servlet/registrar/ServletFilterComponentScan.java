package cn.nihility.plugin.servlet.registrar;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @see org.springframework.boot.web.servlet.ServletComponentScan
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ServletFilterBeanDefinitionRegistrar.class)
public @interface ServletFilterComponentScan {

    /**
     * Base packages to scan for annotated servlet filter components.
     */
    String[] basePackages() default {};

}
