package cn.nihility.boot.selector;

import cn.nihility.boot.config.UserConfiguration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * UserImportSelector
 *
 * @author dandelion
 * @date 2020-04-25 12:58
 */
public class UserImportSelector implements ImportSelector {
    /* 根据获取配置类的名称 */
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {UserConfiguration.class.getName()};
    }
}
