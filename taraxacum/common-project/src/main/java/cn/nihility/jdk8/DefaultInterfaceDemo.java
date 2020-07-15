package cn.nihility.jdk8;

/**
 * DefaultInterfaceDemo
 *
 * @author clover
 * @date 2020-01-04 17:15
 */
public class DefaultInterfaceDemo {

    public static void main(String[] args) {

        DefaultInterfaceImpl defaultInterface = new DefaultInterfaceImpl();
        defaultInterface.myMethod();

        System.out.println("==========================");
        DefaultInterfaceImpl02 defaultInterfaceImpl02 = new DefaultInterfaceImpl02();
        defaultInterfaceImpl02.myMethod();

    }


}
