package cn.nihility.jdk8.proxy;

/**
 * HelloImpl2
 *
 * @author dandelion
 * @date 2020-04-13 13:36
 */
public class HelloImpl2 implements IHello {
    @Override
    public void sayHello() {
        System.out.println("Hello Impl 2 say hello.");
    }
}
