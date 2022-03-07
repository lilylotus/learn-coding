package cn.nihility.plugin.servlet.registrar;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @see org.springframework.boot.web.servlet.ServletComponentScanRegistrar
 */
public class ServletFilterBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(ServletFilterRegisteringPostProcessor.class);
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(getPackagesToScan(metadata));
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(ServletFilterBeanDefinitionRegistrar.class.getName(), beanDefinition);

    }

    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes
            .fromMap(metadata.getAnnotationAttributes(ServletFilterComponentScan.class.getName()));
        String[] basePackages = Optional.ofNullable(attributes)
            .map(v -> v.getStringArray("basePackages")).orElse(new String[0]);
        Set<String> packagesToScan = new LinkedHashSet<>(Arrays.asList(basePackages));
        if (packagesToScan.isEmpty()) {
            packagesToScan.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return packagesToScan;
    }

}
