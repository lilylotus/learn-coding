package cn.nihility.jdk8.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * MyMethodInterceptor
 *
 * @author dandelion
 * @date 2020-04-13 14:15
 */
public class MyMethodInterceptor implements MethodInterceptor {

    /**
     * sub : cglib 生成的代理对象
     * method: 被代理的对象方法
     * args: 方法入入参
     * methodProxy: 代理方法
     */
    @Override
    public Object intercept(Object sub, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("intercept before");
        final Object result = methodProxy.invokeSuper(sub, args);
        System.out.println("intercept after");
        return result;
    }
}
