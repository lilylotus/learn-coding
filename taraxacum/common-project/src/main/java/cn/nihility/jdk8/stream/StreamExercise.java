package cn.nihility.jdk8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * StreamExercise
 *
 * @author clover
 * @date 2020-01-04 18:56
 */
public class StreamExercise {

    // 找出流中大于 2 的元素，然后就每个元素乘以 2，在忽略掉前两个元素，然后取出前两个元素，最后求出总和。
    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1, 2, 3, 1, 23, 34, 12, 456, 123, 45);

        Optional<Integer> reduce = list.stream().filter(item -> item > 2)
                .map(item -> 2 * item).skip(2).limit(2).reduce(Integer::sum);
        reduce.ifPresent(System.out::println);

        int sum = list.stream().filter(item -> item > 2)
                .mapToInt(item -> item * 2).skip(2).limit(2).sum();
        //.forEach(System.out::println);
        System.out.println(sum);

    }

}
