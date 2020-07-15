package cn.nihility.jdk8;

/**
 * DefaultInterfaceImpl02
 *
 * @author clover
 * @date 2020-01-04 17:16
 */
public class DefaultInterfaceImpl02 implements DefaultInterface, DefaultInterface02 {

    @Override
    public void myMethod() {
        System.out.println("Override myMethod");
        // 仅使用特定接口的重复方法
        DefaultInterface02.super.myMethod();
    }

}
