package cn.nihility.cloud.openfeign.proxy.factory;

import cn.nihility.cloud.openfeign.proxy.registry.ProxyRegistryFeignMethodInterceptor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;

public class ProxyFeignClientFactoryBean implements FactoryBean<Object> {

    private Class<?> type;
    private FactoryBean<Object> proxy;
    private Object proxyObjectEnhancer;
    private ListableBeanFactory beanFactory;

    public ProxyFeignClientFactoryBean(FactoryBean<Object> proxy, Class<?> type, ListableBeanFactory beanFactory) {
        this.proxy = proxy;
        this.type = type;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getObject() {
        if (null == proxyObjectEnhancer) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(type);
            enhancer.setCallback(new ProxyRegistryFeignMethodInterceptor(proxy, type, beanFactory));
            proxyObjectEnhancer = enhancer.create();
        }
        return proxyObjectEnhancer;
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public void setProxy(FactoryBean<Object> proxy) {
        this.proxy = proxy;
    }

    public void setBeanFactory(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
