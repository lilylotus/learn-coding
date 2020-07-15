package cn.nihility.jdk8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * StreamCloseDemo
 *
 * @author clover
 * @date 2020-01-05 20:27
 */
public class StreamCloseDemo {


    public static void main(String[] args) {

        List<String> list = Arrays.asList("hello", "world", "welcome");

        NullPointerException nullPointerException = new NullPointerException("self exception");

        try (Stream<String> stream = list.stream()) {
            stream.onClose(() -> {
                System.out.println("aaaa");
                throw new NullPointerException("first Null Exception");
            }).onClose(() -> {
                System.out.println("bbbb");
                throw new NullPointerException("second Null Exception");
            }).forEach(System.out::println);

        }

    }


}
