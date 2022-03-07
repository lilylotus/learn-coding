package cn.nihility.demo.post.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.Set;

/**
 * @author sakura
 * @date 2022-03-02 23:21
 */
public class ProxyAutoScanConfigurationImpl implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(ProxyAutoScanConfigurationImpl.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(ProxyAutoScan.class.getName()));
        String[] scanPackages = Optional.ofNullable(attributes)
            .map(v -> v.getStringArray("scanPackages"))
            .orElse(new String[0]);

        if (ObjectUtils.isEmpty(scanPackages)) {
            log.info("proxy auto scan package is empty");
            return;
        }

        ProxyAutoClassPathBeanDefinitionScanner scanner = new ProxyAutoClassPathBeanDefinitionScanner(registry);
        scanner.scan(scanPackages);

    }

    /**
     * @see ClassPathScanningCandidateComponentProvider
     */
    static class ProxyAutoClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

        private static final Logger LOGGER = LoggerFactory.getLogger(ProxyAutoClassPathBeanDefinitionScanner.class);

        // Copy of FactoryBean#OBJECT_TYPE_ATTRIBUTE which was added in Spring 5.2
        static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";

        public ProxyAutoClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
            super(registry, false);
            registryIncludeFilters();
        }

        private void registryIncludeFilters() {

            addIncludeFilter((metadataReader, metadataReaderFactory) -> {
                boolean hasAnnotation = metadataReader.getAnnotationMetadata()
                    .hasAnnotation(ProxyService.class.getName());
                boolean anInterface = metadataReader.getClassMetadata().isInterface();
                return hasAnnotation && anInterface;
            });

        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);

            if (beanDefinitionHolders.isEmpty()) {
                LOGGER.warn("No proxy service in proxy auto scan packages");
            } else {
                processBeanDefinitions(beanDefinitionHolders);
            }

            return beanDefinitionHolders;
        }

        private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {

            AbstractBeanDefinition beanDefinition;
            BeanDefinitionRegistry registry = getRegistry();
            Assert.notNull(registry, "BeanDefinitionRegistry cannot be null");

            for (BeanDefinitionHolder holder : beanDefinitionHolders) {

                beanDefinition = (AbstractBeanDefinition) holder.getBeanDefinition();
                boolean scopedProxy = false;
                if (ScopedProxyFactoryBean.class.getName().equals(beanDefinition.getBeanClassName())) {
                    beanDefinition = (AbstractBeanDefinition) Optional
                        .ofNullable(((RootBeanDefinition) beanDefinition).getDecoratedDefinition())
                        .map(BeanDefinitionHolder::getBeanDefinition).orElseThrow(() -> new IllegalStateException(
                            "The target bean definition of scoped proxy bean not found. Root bean definition[" + holder + "]"));
                    scopedProxy = true;
                }

                String beanClassName = beanDefinition.getBeanClassName();
                LOGGER.info("Creating ProxyFactoryBean with the name [{}] and [{}] interface",
                    holder.getBeanName(), beanClassName);

                // the mapper interface is the original class of the bean
                // but, the actual class of the bean is ProxyFactoryBean
                beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
                beanDefinition.setBeanClass(ProxyFactoryBean.class);

                beanDefinition.getPropertyValues().add("tip", "Proxy Factory Bean Create Tip Message");

                // Attribute for MockitoPostProcessor
                // https://github.com/mybatis/spring-boot-starter/issues/475
                beanDefinition.setAttribute(FACTORY_BEAN_OBJECT_TYPE, beanClassName);

                LOGGER.info("Enabling autowire by type for MapperFactoryBean with name '{}'.", holder.getBeanName());
                beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

                //beanDefinition.setLazyInit(false);

                if (scopedProxy) {
                    continue;
                }

                /*if (ConfigurableBeanFactory.SCOPE_SINGLETON.equals(beanDefinition.getScope()) && defaultScope != null) {
                    beanDefinition.setScope(defaultScope);
                }*/

                if (!beanDefinition.isSingleton()) {
                    BeanDefinitionHolder proxyHolder = ScopedProxyUtils.createScopedProxy(holder, registry, true);
                    if (registry.containsBeanDefinition(proxyHolder.getBeanName())) {
                        registry.removeBeanDefinition(proxyHolder.getBeanName());
                    }
                    registry.registerBeanDefinition(proxyHolder.getBeanName(), proxyHolder.getBeanDefinition());
                }

            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            AnnotationMetadata metadata = beanDefinition.getMetadata();
            return metadata.isInterface() && metadata.isIndependent();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
            if (super.checkCandidate(beanName, beanDefinition)) {
                return true;
            } else {
                LOGGER.warn("Skipping ProxyFactoryBean with name '{}' and '{}' proxyInterface. Bean already defined with the same name!",
                    beanName, beanDefinition.getBeanClassName());
                return false;
            }
        }

    }

}
