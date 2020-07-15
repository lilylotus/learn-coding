package cn.nihility.jdk8.proxy;

/**
 * HelloImpl
 *
 * @author dandelion
 * @date 2020-04-13 13:21
 */
public class HelloImpl implements IHello {
    @Override
    public void sayHello() {
        System.out.println("HelloImpl Say Hello.");
    }
}
