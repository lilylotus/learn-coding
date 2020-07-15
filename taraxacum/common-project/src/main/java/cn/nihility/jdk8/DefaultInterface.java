package cn.nihility.jdk8;

/**
 * DefaultInterface
 *
 * @author clover
 * @date 2020-01-04 17:14
 */
public interface DefaultInterface {

    default void myMethod() {
        System.out.println("DefaultInterface myMethod");
    }

}
