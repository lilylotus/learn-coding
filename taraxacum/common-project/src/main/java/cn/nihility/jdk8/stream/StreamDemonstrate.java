package cn.nihility.jdk8.stream;


import cn.nihility.util.AlgorithmUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * StreamDemonstrate
 *
 * @author clover
 * @date 2020-02-26 17:54
 */
public class StreamDemonstrate {

    public static void main(String[] args) {
        List<Integer> list = AlgorithmUtil.randomIntegerArrayList(10, 10, 100);
        ArrayList<Integer> list2 = AlgorithmUtil.randomIntegerArrayList(10, 10, 100);
        list.forEach(a -> System.out.print(a + " "));
        System.out.println();

        System.out.println("----------------------------");
        list.forEach(a -> System.out.print(a + " "));
        System.out.println();

        System.out.println("----------------------------");
        Stream<List<Integer>> sl = Stream.of(list, list2);
        sl.flatMap(Collection::stream)
                .forEach(a -> System.out.print(a + " "));

    }

}
