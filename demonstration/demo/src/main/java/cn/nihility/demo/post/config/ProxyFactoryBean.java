package cn.nihility.demo.post.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author sakura
 * @date 2022-03-03 00:21
 */
public class ProxyFactoryBean<T> implements FactoryBean<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyFactoryBean.class);

    private Class<T> proxyInstance;

    private String tip;

    public ProxyFactoryBean() {
    }

    public ProxyFactoryBean(Class<T> proxyInstance) {
        this.proxyInstance = proxyInstance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        LOGGER.info("ProxyFactoryBean get object [{}] instance, tip [{}]", proxyInstance.getName(), tip);
        return (T) Proxy.newProxyInstance(proxyInstance.getClassLoader(),
            new Class[]{proxyInstance}, new ProxyInvocationHandler(proxyInstance));
    }

    @Override
    public Class<?> getObjectType() {
        return proxyInstance;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

}
