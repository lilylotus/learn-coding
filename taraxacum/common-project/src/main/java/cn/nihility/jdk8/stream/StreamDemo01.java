package cn.nihility.jdk8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * StreamDemo01
 *
 * @author clover
 * @date 2020-01-04 17:38
 */
public class StreamDemo01 {

    public static void main(String[] args) {
        // 创建方式 1
        Stream stream = Stream.of("hello", "one", "two", "three");

        // 创建方式 2
        String[] array = new String[] {"hello", "one", "two", "three"};
        Stream stream1 = Stream.of(array);
        Stream stream2 = Arrays.stream(array); // 和上一种是一样的

        // 创建方式 3
        List<String> list = Arrays.asList("hello", "one", "two", "three");
        Stream<String> stream3 = list.stream();

        IntStream.of(5, 6, 7).forEach(System.out::println);
        System.out.println("=====================");
        IntStream.range(1, 3).forEach(System.out::println);
        System.out.println("=====================");
        IntStream.rangeClosed(1, 3).forEach(System.out::println);

        System.out.println("===================");
        int reduce = IntStream.range(1, 3).map(v -> 2 * v).reduce(0, Integer::sum);
        System.out.println("1 - 9 sum = " + reduce);

    }

}
