package cn.nihility.jdk8;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Test1
 *
 * @author clover
 * @date 2020-01-04 10:46
 */
public class Test1 {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        for (int i = 0; i < 10; i++) {
            System.out.println(list.get(i));
        }
        System.out.println("-----------------------------------");

        for (Integer i : list) {
            System.out.println(i);
        }
        System.out.println("-----------------------------------");

        list.forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(integer * 2);
            }
        });
        System.out.println("-----------------------------------");

        list.forEach( i -> System.out.println(i) );
        System.out.println("-----------------------------------");

        list.forEach(System.out::println);



    }

}
