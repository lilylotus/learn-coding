package cn.nihility.cloud.openfeign.proxy.registry;

import cn.nihility.cloud.openfeign.proxy.annotation.LocalFeignClient;
import cn.nihility.cloud.openfeign.proxy.annotation.RemoteFeignInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyRegistryFeignMethodInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ProxyRegistryFeignMethodInterceptor.class);

    private Class<?> type;
    private BeanFactory beanFactory;
    private Object originFeignClient;

    private Object proxyInstance;

    public ProxyRegistryFeignMethodInterceptor(Object originFeignClient, Class<?> type, BeanFactory beanFactory) {
        this.type = type;
        this.beanFactory = beanFactory;
        this.originFeignClient = originFeignClient;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(originFeignClient, args);
        }

        logger.info("invoke method [{}]", method.getName());
        LocalFeignClient clientAnnotation = type.getAnnotation(LocalFeignClient.class);
        RemoteFeignInvoke remoteInvokeAnnotation = method.getAnnotation(RemoteFeignInvoke.class);
        if (null == clientAnnotation || null != remoteInvokeAnnotation) {
            return method.invoke(originFeignClient, args);
        } else {
            return invokeProxyMethod(clientAnnotation, method, args);
        }

    }

    private Object invokeProxyMethod(LocalFeignClient clientAnnotation, Method method, Object[] args)
        throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class<?> proxyClazz = clientAnnotation.localClass();
        String beanName = clientAnnotation.beanName();

        if (proxyInstance == null) {
            if (StringUtils.hasText(beanName)) {
                proxyInstance = beanFactory.getBean(beanName, proxyClazz);
            } else {
                proxyInstance = BeanUtils.instantiateClass(proxyClazz);
            }
        }

        String clazzMethodName = method.getName();
        Class<?>[] methodParameterTypes = method.getParameterTypes();
        Class<?> methodReturnType = method.getReturnType();
        Method proxyMethod;
        try {
            proxyMethod = proxyClazz.getDeclaredMethod(clazzMethodName, methodParameterTypes);
        } catch (NoSuchMethodException e) {
            logger.error("Feign [{}] 代理 [{}.{}] 方法不存在", type.getName(), proxyClazz.getName(), method.getName());
            throw e;
        }

        Class<?> proxyMethodReturnType = proxyMethod.getReturnType();
        if (methodReturnType.equals(proxyMethodReturnType)) {
            logger.error("手动代理 Feign [{}.{}] 返回值和原 Feign [{}.{}] 不匹配",
                proxyClazz.getName(), clazzMethodName, type.getName(), clazzMethodName);
            throw new IllegalArgumentException("手动代理 Feign 和原 Feign 接口返回值类型不一致");
        }

        try {
            return proxyMethod.invoke(proxyInstance, args);
        } catch (Exception ex) {
            logger.error("Proxy Feign Client Invoke Error, 原方法 [{}.{}], 代理方法 [{}.{}]",
                type.getName(), method.getName(), proxyClazz.getName(), method.getName());
            throw ex;
        }
    }

}
