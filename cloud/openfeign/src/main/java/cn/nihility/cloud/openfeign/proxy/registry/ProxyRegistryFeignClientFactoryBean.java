package cn.nihility.cloud.openfeign.proxy.registry;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;

public class ProxyRegistryFeignClientFactoryBean implements FactoryBean<Object> {

    private Class<?> type;
    private Object proxyInstance;
    private BeanFactory beanFactory;
    private Object proxyInstanceEnhancer;

    @Override
    public Object getObject() {
        if (null == proxyInstanceEnhancer) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(type);
            enhancer.setCallback(new ProxyRegistryFeignMethodInterceptor(proxyInstance, type, beanFactory));
            proxyInstanceEnhancer = enhancer.create();
        }
        return proxyInstanceEnhancer;
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public void setProxyInstance(Object proxyInstance) {
        this.proxyInstance = proxyInstance;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}
