package cn.nihility.jdk8.stream;

import java.util.Arrays;
import java.util.List;

/**
 * StreamDemo12
 *
 * @author clover
 * @date 2020-01-06 10:04
 */
public class StreamDemo12 {

    public static void main(String[] args) {

        List<String> list = Arrays.asList("hello", "world", "welcome");

        System.out.println(list.getClass());

//        list.stream().forEach(System.out::println);

        list.stream().map(item -> item).forEach(System.out::println);

    }

}
