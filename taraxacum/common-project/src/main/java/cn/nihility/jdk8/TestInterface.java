package cn.nihility.jdk8;

/**
 * TestInterface
 *
 * @author clover
 * @date 2020-01-04 11:09
 */

@FunctionalInterface
public interface TestInterface {

    void test();

    String toString(); // 因为 toString() 是有 Object 类来的，不会为抽象方法数据增加
    /**
     * java.lang.Object 所有的类都继承于超类 Object 所以 String toString(); 仅能算是重写
     */

    // String myString(); 违反了函数式接口仅可以有一个抽象方法的要求

}
