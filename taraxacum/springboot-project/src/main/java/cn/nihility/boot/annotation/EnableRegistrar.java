package cn.nihility.boot.annotation;

import cn.nihility.boot.registrar.BeanImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author dandelion
 * @date 2020:06:27 19:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(BeanImportBeanDefinitionRegistrar.class)
public @interface EnableRegistrar {
}
