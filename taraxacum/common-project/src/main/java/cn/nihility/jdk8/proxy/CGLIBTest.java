package cn.nihility.jdk8.proxy;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;

/**
 * CGLIBTest
 *
 * @author dandelion
 * @date 2020-04-13 14:19
 */
public class CGLIBTest {

    public static void main(String[] args) {
        String rootPath = CGLIBTest.class.getResource("/").getPath();
        System.out.println("root path : " + rootPath);

        // 1. 代理类 Class 文件存入本地磁盘，方便反编译查看源码
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, rootPath);

        // CGLIB 动态代理获取对象
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloService.class);
        enhancer.setCallback(new MyMethodInterceptor());

        final HelloService service = (HelloService) enhancer.create();
        service.sayHello();
    }

}
