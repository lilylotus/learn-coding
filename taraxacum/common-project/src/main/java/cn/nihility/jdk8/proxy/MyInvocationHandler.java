package cn.nihility.jdk8.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * MyInvocationHandler
 *
 * @author dandelion
 * @date 2020-04-13 13:22
 */
public class MyInvocationHandler implements InvocationHandler {

    private Object target; // 要代理的目标对象

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("--------------- invoke before ---------------");
        final Object result = method.invoke(target, args);
        System.out.println("--------------- invoke end ---------------");
        return result;
    }
}
