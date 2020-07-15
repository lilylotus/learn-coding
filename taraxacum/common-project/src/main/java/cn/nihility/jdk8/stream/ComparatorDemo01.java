package cn.nihility.jdk8.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ComparatorDemo01
 *
 * @author clover
 * @date 2020-01-05 11:19
 */
public class ComparatorDemo01 {

    public static void main(String[] args) {

        List<String> list = Arrays.asList("nihao", "hello", "world", "welcome");

       /* Collections.sort(list, (item1, item2) -> item1.length() - item2.length());
        System.out.println(list);

        System.out.println("============================");
        Collections.sort(list, (item1, item2) -> item2.length() - item1.length());
        System.out.println(list);

        System.out.println("============================");
        Collections.sort(list, Comparator.comparingInt(String::length).reversed());
        System.out.println(list);

        System.out.println("============================");
        Collections.sort(list, Comparator.comparingInt((String item) -> item.length()).reversed());
        System.out.println(list);

        System.out.println("============================");
        Collections.sort(list, Comparator.comparingInt(String::length).thenComparing(String.CASE_INSENSITIVE_ORDER));
        System.out.println(list);*/

        System.out.println("============================");
        Collections.sort(list, Comparator.comparingInt((String::length)).thenComparing((item1, item2) -> item1.toLowerCase().compareTo(item2.toLowerCase())));
        System.out.println(list);

    }

}
