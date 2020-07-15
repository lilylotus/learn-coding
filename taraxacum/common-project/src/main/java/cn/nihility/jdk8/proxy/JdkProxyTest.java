package cn.nihility.jdk8.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * JdkProxyTest
 *
 * @author dandelion
 * @date 2020-04-13 13:24
 */
public class JdkProxyTest {

    public static void main(String[] args) throws Exception {

        // 方式一
        // 1. 生成 $Proxy0 的 class 文件
        System.getProperties().put("sum.misc.ProxyGenerator.saveGeneratedFiles", "true");
        // 2. 获取动态代理类
        final Class<?> proxyClass = Proxy.getProxyClass(IHello.class.getClassLoader(), IHello.class);
        // 3. 获取代理类的构造函数，并传入参数类型 InvocationHandler.class
        final Constructor<?> proxyClassConstructor = proxyClass.getConstructor(InvocationHandler.class);
        // 4. 通过构造函数创建代理对象，将自己的 InvocationHandler 实例传入
        final IHello iHello = (IHello) proxyClassConstructor.newInstance(new MyInvocationHandler(new HelloImpl2()));
        // 5. 通过代理对象调用目标方法
        System.out.println(iHello);
        iHello.sayHello();

        // 方式2
        /**
         * 直接使用 newProxyInstance 将 2~4 步骤合并
         */
        System.out.println("=================================");
        /*final Class<?> clazz = Class.forName("cn.nihility.jdk8.proxy.HelloImpl");
        final IHello instance = (IHello) clazz.newInstance();
        final IHello o = (IHello) Proxy.newProxyInstance(IHello.class.getClassLoader(),
                new Class<?>[]{IHello.class},
                (proxy, method, params) -> {
                    System.out.println("---------- newProxyInstance before ----------");
                    final Object result = method.invoke(instance, params);
                    System.out.println("---------- newProxyInstance end ----------");
                    return result;
                });*/

        final IHello o = (IHello) Proxy.newProxyInstance(IHello.class.getClassLoader(),
                new Class<?>[]{IHello.class},
                new MyInvocationHandler(new HelloImpl()));

        System.out.println(o);
        o.sayHello();

    }

}
