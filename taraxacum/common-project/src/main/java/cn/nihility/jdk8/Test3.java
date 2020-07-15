package cn.nihility.jdk8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test3
 *
 * @author clover
 * @date 2020-01-04 11:36
 */
public class Test3 {

    public static void main(String[] args) {

        /**
         * 函数式接口可以通过三种方式实现
         * 1. Lambda 表达式
         * 2. 方法引用
         * 3. 构造方法引用
         */

        // 1.
        MyInterface30 i3 = () -> {};
        System.out.println(i3.getClass().getInterfaces()[0]);

        MyInterface31 i31 = () -> {}; // () -> {} 必须要通过上下文的信息来处理， lambda 仅关系方法的参数和返回，对于方法名称不关心
        System.out.println(i31.getClass().getInterfaces()[0]);

        Thread thread = new Thread(() -> System.out.println("Hello Thread"));
        thread.start();

        System.out.println("========================");

        List<String> list = Arrays.asList("Hello", "world", "Hello World");
        list.forEach( item -> System.out.println(item.toUpperCase()));

        System.out.println("==========================");
        List<String> list2 = new ArrayList<>();
        list.forEach( item -> list2.add(item.toUpperCase()) );

        System.out.println("==========================");
        list2.forEach( item -> System.out.println(item) );

        // 流方式 stream(),  parallelStream() 并行
        System.out.println("============ lambda implement ==============");
        list.stream().map( item -> item.toUpperCase() ).forEach( item -> System.out.println(item) );
        System.out.println("========== method reference =============");
        list.stream().map( String::toUpperCase ).forEach( System.out::println );



    }

}

@FunctionalInterface
interface MyInterface30 {

    void myMethod1();

}

@FunctionalInterface
interface MyInterface31 {

    void myMethod2();

}