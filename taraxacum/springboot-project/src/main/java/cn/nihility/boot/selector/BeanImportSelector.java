package cn.nihility.boot.selector;

import cn.nihility.boot.annotation.ImportSelectorScan;
import cn.nihility.boot.suports.IAutoImportFactory;
import cn.nihility.boot.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 自动注册方式
 * 1. 添加到 spring.factories
 *    org.springframework.boot.autoconfigure.EnableAutoConfiguration=cn.nihility.boot.selector.BeanImportSelector
 * 2. 使用注解 @EnableBeanImportSelector 到 @Configuration 类 -> 有 @Import(BeanImportSelector.class) 会自动注入
 * 3. 直接在类上配置 @Configuration 但是要到 spring 能有扫描的到地方
 *
 * @author dandelion
 * @date 2020:06:27 16:30
 */
public class BeanImportSelector implements ImportSelector {

    private final static Logger log = LoggerFactory.getLogger(BeanImportSelector.class);

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        /* 返回的 class name 数组为要加载的 bean */
        final List<String> clazzNameList = new ArrayList<>();

        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(ImportSelectorScan.class.getName(), true));
        if (attributes != null) {
            String[] basePackages = attributes.getStringArray("basePackage");
            if (basePackages != null && basePackages.length > 0) {
                String scanPackagesString = String.join(":", basePackages);
                log.info("BeanImportSelector scan package [{}]", scanPackagesString);

                Stream.of(basePackages).forEach(p -> clazzNameList.addAll(ResourceUtil.scanPackageClassName(p)));
            }
        }

        log.info("Import class [{}]", String.join(":", clazzNameList));

        List<String> factoriesClassNameList = ResourceUtil.loadFactoriesClassName(IAutoImportFactory.class);
        clazzNameList.addAll(factoriesClassNameList);

        log.info("Factories class [{}]", String.join(":", factoriesClassNameList));

        return clazzNameList.toArray(new String[0]);
    }
}
