package cn.nihility.boot.registrar;

import cn.nihility.boot.annotation.RegistrarScan;
import cn.nihility.boot.registrar.dto.TestRegistrar;
import cn.nihility.boot.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dandelion
 * @date 2020:06:27 18:14
 */
public class BeanImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware, ResourceLoaderAware {

    private final static Logger log = LoggerFactory.getLogger(BeanImportBeanDefinitionRegistrar.class);
    private static ResourceLoader resourceLoader;
    private static BeanFactory beanFactory;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        BeanImportBeanDefinitionRegistrar.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        log.info("BeanImportBeanDefinitionRegistrar -> registerBeanDefinitions");

        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RegistrarScan.class.getName()));

        if (null != attributes) {
            String[] packages = attributes.getStringArray("basePackage");
            if (packages.length > 0) {
                log.info("scan package [{}]", String.join(":", packages));
                List<Class<?>> clazzList = ResourceUtil.scanPackageClass(packages[0], 1);
                String clazzString = clazzList.stream().map(Class::getName).collect(Collectors.joining(":"));
                log.info("registrar class name [{}]", clazzString);

                clazzList.forEach(c -> {
                    if (c.isInterface()) {
                        registryBean(c, registry);
                    }
                });
            }
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(TestRegistrar.class);
        builder.addPropertyValue("name", "test registrar name");
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition("testRegistrar", beanDefinition);
    }

    private void registryBean(Class<?> clazz, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperFactoryBean.class);
        builder.addPropertyValue("mapperInstance", clazz);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(getClass().getSimpleName() + "#" + clazz.getSimpleName(), beanDefinition);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        BeanImportBeanDefinitionRegistrar.beanFactory = beanFactory;
    }

    public static BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
