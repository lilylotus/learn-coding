package cn.nihility.plugin.servlet.registrar;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebFilter;
import java.util.*;

/**
 * {@link org.springframework.boot.web.servlet.ServletComponentRegisteringPostProcessor}
 * <p>
 * 添加 {@link Order} 注解，定制 servlet filter 的执行顺序
 */
public class ServletFilterRegisteringPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Set<String> scanPackages;

    public ServletFilterRegisteringPostProcessor(Set<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (isRunningInEmbeddedWebServer()) {
            ClassPathScanningCandidateComponentProvider provider = createComponentProvider();
            for (String basePackage : scanPackages) {
                Set<BeanDefinition> definitions = provider.findCandidateComponents(basePackage);
                for (BeanDefinition definition : definitions) {
                    if (definition instanceof AnnotatedBeanDefinition) {
                        AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) definition;

                        Map<String, Object> filterAttributes = Optional.ofNullable(beanDefinition.getMetadata()
                            .getAnnotationAttributes(WebFilter.class.getName())).orElse(new HashMap<>(0));
                        Map<String, Object> orderAttributes = Optional.ofNullable(beanDefinition.getMetadata()
                            .getAnnotationAttributes(Order.class.getName())).orElse(new HashMap<>(0));

                        filterAttributes.put("orderValue",
                            orderAttributes.getOrDefault("value", Ordered.LOWEST_PRECEDENCE));

                        doHandle(filterAttributes, beanDefinition, (BeanDefinitionRegistry) this.applicationContext);
                    }
                }
            }
        }

    }

    private boolean isRunningInEmbeddedWebServer() {
        return this.applicationContext instanceof WebApplicationContext
            && ((WebApplicationContext) this.applicationContext).getServletContext() == null;
    }

    private ClassPathScanningCandidateComponentProvider createComponentProvider() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.setEnvironment(this.applicationContext.getEnvironment());
        provider.setResourceLoader(this.applicationContext);
        provider.addIncludeFilter(new AnnotationTypeFilter(WebFilter.class));
        return provider;
    }

    public void doHandle(Map<String, Object> attributes, AnnotatedBeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(FilterRegistrationBean.class);
        builder.addPropertyValue("asyncSupported", attributes.get("asyncSupported"));
        builder.addPropertyValue("dispatcherTypes", extractDispatcherTypes(attributes));
        builder.addPropertyValue("filter", beanDefinition);
        builder.addPropertyValue("initParameters", extractInitParameters(attributes));
        String name = determineName(attributes, beanDefinition);
        builder.addPropertyValue("name", name);
        builder.addPropertyValue("order", attributes.get("orderValue"));
        builder.addPropertyValue("servletNames", attributes.get("servletNames"));
        builder.addPropertyValue("urlPatterns", extractUrlPatterns(attributes));
        registry.registerBeanDefinition(name, builder.getBeanDefinition());
    }

    protected final Map<String, String> extractInitParameters(Map<String, Object> attributes) {
        Map<String, String> initParameters = new HashMap<>();
        for (AnnotationAttributes initParam : (AnnotationAttributes[]) attributes.get("initParams")) {
            String name = (String) initParam.get("name");
            String value = (String) initParam.get("value");
            initParameters.put(name, value);
        }
        return initParameters;
    }


    protected String[] extractUrlPatterns(Map<String, Object> attributes) {
        String[] value = (String[]) attributes.get("value");
        String[] urlPatterns = (String[]) attributes.get("urlPatterns");
        if (urlPatterns.length > 0) {
            Assert.state(value.length == 0, "The urlPatterns and value attributes are mutually exclusive.");
            return urlPatterns;
        }
        return value;
    }

    private EnumSet<DispatcherType> extractDispatcherTypes(Map<String, Object> attributes) {
        DispatcherType[] dispatcherTypes = (DispatcherType[]) attributes.get("dispatcherTypes");
        if (dispatcherTypes.length == 0) {
            return EnumSet.noneOf(DispatcherType.class);
        }
        if (dispatcherTypes.length == 1) {
            return EnumSet.of(dispatcherTypes[0]);
        }
        return EnumSet.of(dispatcherTypes[0], Arrays.copyOfRange(dispatcherTypes, 1, dispatcherTypes.length));
    }

    private String determineName(Map<String, Object> attributes, BeanDefinition beanDefinition) {
        return (String) (StringUtils.hasText((String) attributes.get("filterName")) ? attributes.get("filterName")
            : beanDefinition.getBeanClassName());
    }


}
