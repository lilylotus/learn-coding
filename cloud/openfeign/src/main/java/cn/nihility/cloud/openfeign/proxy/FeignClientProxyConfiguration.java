package cn.nihility.cloud.openfeign.proxy;

import cn.nihility.cloud.openfeign.proxy.annotation.LocalFeignClient;
import cn.nihility.cloud.openfeign.proxy.factory.ProxyFeignClientFactoryBean;
import cn.nihility.cloud.openfeign.proxy.registry.ProxyRegistryFeignClientFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * beanFactory -> postProcessBeanDefinitionRegistry -> postProcessBeanFactory
 */
@Configuration(proxyBeanMethods = false)
@SuppressWarnings({"AlibabaRemoveCommentedCode", "java:S125"})
public class FeignClientProxyConfiguration implements BeanFactoryPostProcessor, BeanDefinitionRegistryPostProcessor, BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(FeignClientProxyConfiguration.class);

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        logger.info("beanFactory");
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("postProcessBeanFactory");
        /* 利用反射替换 spring 容器 bean 实例 */
        /*String[] names = beanFactory.getBeanNamesForAnnotation(FeignClient.class);
        for (String name : names) {
            try {
                Class<?> clazz = ClassUtils.forName(name, beanFactory.getBeanClassLoader());
                if (clazz.isAnnotationPresent(LocalFeignClient.class)) {
                    logger.info("proxy feign client class [{}]", name);
                    proxyFeignClient(beanFactory, name, clazz);
                }
            } catch (ClassNotFoundException e) {
                logger.error("Class [{}] forName", names, e);
            }
        }*/
    }

    @SuppressWarnings({"unchecked", "java:S4449", "java:S2259"})
    private void proxyFeignClient(ConfigurableListableBeanFactory beanFactory, String beanName, Class<?> type) {
        Field field = ReflectionUtils.findField(DefaultSingletonBeanRegistry.class, "singletonObjects");
        ReflectionUtils.makeAccessible(field);
        Map<String, Object> singletonObjects = (Map<String, Object>) ReflectionUtils.getField(field, beanFactory);
        // FeignClientFactoryBean - FactoryBean<Object>
        FactoryBean<Object> bean = (FactoryBean<Object>) singletonObjects.remove(beanName);
        singletonObjects.put(beanName, new ProxyFeignClientFactoryBean(bean, type, beanFactory));
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        logger.info("postProcessBeanDefinitionRegistry");
        String[] feignClientBeanNames = beanFactory.getBeanNamesForAnnotation(FeignClient.class);
        for (String beanName : feignClientBeanNames) {
            renewFeignClient(registry, beanName);
        }
    }

    private void renewFeignClient(BeanDefinitionRegistry registry, String beanName) {
        Class<?> feignClazz = null;
        try {
            feignClazz = ClassUtils.forName(beanName, registry.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            logger.error("feign 代理类 [{}] 不存在", beanName, e);
        }
        if (feignClazz == null || !feignClazz.isAnnotationPresent(LocalFeignClient.class)) {
            logger.info("FeignClient [{}] 不需要重写代理", beanName);
            return;
        }

        logger.info("proxy feign client bean class name [{}]", beanName);

        /*BeanDefinition originBeanDefinition = registry.getBeanDefinition(beanName);
        MutablePropertyValues originPropertyValues = originBeanDefinition.getPropertyValues();
        String type = Objects.toString(originPropertyValues.get("type"), "");
        String originProxyBeanName = type + "#proxy";
        // 删除原始的 FeignClient Bean 定义 FeignClientsRegistrar.registerFeignClient()
        registry.removeBeanDefinition(type);
        // 重写原始 FeignClient Bean 的名称，重新注册，防止合随后要用的冲突
        registry.registerBeanDefinition(originProxyBeanName, originBeanDefinition);
        // 获取原始 FeignClient 的代理对象示例，让 spring 容器生成一个原对应的 FeignClient 代理 feign.Target.HardCodedTarget
        Object originProxy = beanFactory.getBean(originProxyBeanName);
        // 移除掉原始 FeignClient 的定义，防止干扰稍后重定义的 FeignClient Bean 定义
        registry.removeBeanDefinition(originProxyBeanName);*/

        // 获取原 spring 容器生成的 FeignClient 代理 feign.Target.HardCodedTarget 实例
        Object originFeignProxyInstance = beanFactory.getBean(beanName);
        // 移除原始的 FeignClient Bean 定义
        registry.removeBeanDefinition(beanName);

        // 新建一个自定义的 FeignClient 代理类
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(ProxyRegistryFeignClientFactoryBean.class);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
        definition.addPropertyValue("type", feignClazz);
        definition.addPropertyValue("proxyInstance", originFeignProxyInstance);
        definition.addPropertyValue("beanFactory", beanFactory);
        registry.registerBeanDefinition(beanName, definition.getBeanDefinition());
    }

}
