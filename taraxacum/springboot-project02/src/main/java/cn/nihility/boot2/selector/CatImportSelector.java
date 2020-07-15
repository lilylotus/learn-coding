package cn.nihility.boot2.selector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author dandelion
 * @date 2020:06:27 16:19
 */
public class CatImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        /* 返回的名称会被加载为 Bean */
        return new String[0];
    }
}
