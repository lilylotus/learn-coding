package cn.nihility.boot.annotation;

import cn.nihility.boot.selector.BeanImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author dandelion
 * @date 2020:06:27 17:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(BeanImportSelector.class)
public @interface EnableBeanImportSelector {
}
