package cn.nihility.jdk8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StreamGroupDemo01
 *
 * @author clover
 * @date 2020-01-04 23:24
 */
public class StreamGroupDemo01 {

    public static void main(String[] args) {

        Student student1 = new Student("lishi", 20, 70);
        Student student2 = new Student("wangwu", 15, 85);
        Student student3 = new Student("zhangsan", 24, 70);
        Student student4 = new Student("jiushi", 19, 85);

        List<Student> list = Arrays.asList(student1, student2, student3, student4);

        Map<String, List<Student>> collect = list.stream().collect(Collectors.groupingBy(Student::getName));
        System.out.println(collect);

        System.out.println("========================");
        Map<Integer, List<Student>> collect1 = list.stream().collect(Collectors.groupingBy(Student::getScore));
        System.out.println(collect1);


        System.out.println("========================");
        /**
         * public static <T, K, A, D>
         *     Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier,
         *                                           Collector<? super T, A, D> downstream) {
         *         return groupingBy(classifier, HashMap::new, downstream);
         *     }
         */
        Map<Integer, Long> collect2 = list.stream().collect(Collectors.groupingBy(Student::getScore, Collectors.counting()));
        System.out.println(collect2);

        System.out.println("======================");
        Map<Integer, Double> collect3 = list.stream().collect(Collectors.groupingBy(Student::getScore, Collectors.averagingDouble(Student::getAge)));
        System.out.println(collect3);

        System.out.println("==============");
        Map<Boolean, List<Student>> collect4 = list.stream().collect(Collectors.partitioningBy(s -> s.getScore() >= 80));
        System.out.println(collect4);

    }

}
