package cn.nihility.jdk8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * StreamDemo03
 *
 * @author clover
 * @date 2020-01-04 18:36
 */
public class StreamDemo03 {

    public static void main(String[] args) {

        List<String> list = Arrays.asList("one", "two", "three", "four", "five");

        list.stream().map(String::toUpperCase)
                .collect(Collectors.toList())
                .forEach(System.out::println);


        System.out.println("==============================");
        Stream<List<Integer>> stream = Stream.of(Arrays.asList(1, 2, 34), Arrays.asList(3, 3, 1), Arrays.asList(23, 12, 4));
        stream.flatMap(theList -> theList.stream())
                .map(item -> item * item)
                .forEach(System.out::println);

        System.out.println("====================");
        Stream<String> stream1 = Stream.generate(UUID.randomUUID()::toString);
        stream1.findFirst().ifPresent(System.out::println);

        System.out.println("=========================");
        Stream.iterate(1, item -> item + 2).limit(6).forEach(System.out::println);

    }

}
