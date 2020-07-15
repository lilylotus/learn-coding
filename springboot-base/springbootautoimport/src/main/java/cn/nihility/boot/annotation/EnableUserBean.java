package cn.nihility.boot.annotation;

import cn.nihility.boot.selector.UserImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * EnableUserBean
 *
 * @author dandelion
 * @date 2020-04-25 12:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
// 帮助导入要的配置类
@Import(UserImportSelector.class)
public @interface EnableUserBean {
}
