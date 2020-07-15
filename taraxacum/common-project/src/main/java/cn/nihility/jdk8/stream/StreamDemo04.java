package cn.nihility.jdk8.stream;

import java.util.Arrays;
import java.util.List;

/**
 * StreamDemo04
 *
 * @author clover
 * @date 2020-01-04 19:58
 */
public class StreamDemo04 {

    public static void main(String[] args) {

        /*List<String> list = new ArrayList<>(5000000);
        for (int i = 0; i < 5000000; i++) {
            list.add(UUID.randomUUID().toString());
        }

        System.out.println("开始");

        long startTime = System.nanoTime();

        list.parallelStream().sorted().count();

        long endTime = System.nanoTime();

        long millis = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("duration " + millis);*/

        List<String> list = Arrays.asList("hello1", "world", "hello world");
        list.stream().mapToInt(item -> {
            int len = item.length();
            System.out.println(item);
            return len;
        }).filter(len -> len == 5).findFirst().ifPresent(System.out::println);


        System.out.println("===========================");
        List<String> list1 = Arrays.asList("hello welcome", "world hello", "hello world", "hello welcome");
        list1.stream().flatMap( item -> Arrays.asList(item.split(" ")).stream())
                .distinct().forEach(System.out::println);

        System.out.println("=====================");
        list1.stream().map(item -> item.split(" "))
                .flatMap(Arrays::stream)
                .distinct().forEach(System.out::println);

        System.out.println("======================");
        List<String> list2 = Arrays.asList("Hei", "Hello", "你好");
        List<String> list3 = Arrays.asList("zhangsan", "lishi", "wangwu", "liliu");
        list2.stream().flatMap(item -> list3.stream().map(item2 -> item + " " + item2)).forEach(System.out::println);
    }

}
