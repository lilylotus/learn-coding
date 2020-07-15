package cn.nihility.jdk8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StreamDemo10
 *
 * @author clover
 * @date 2020-01-04 23:45
 */
public class StreamDemo10 {

    public static void main(String[] args) {

        Student student1 = new Student("lishi", 20, 70);
        Student student2 = new Student("wangwu", 15, 85);
        Student student3 = new Student("zhangsan", 24, 70);
        Student student4 = new Student("jiushi", 19, 85);

        List<Student> list = Arrays.asList(student1, student2, student3, student4);

        List<Student> collect = list.stream().collect(Collectors.toList());
        System.out.println(collect);

        System.out.println("=======================");
        System.out.println("count " + list.stream().collect(Collectors.counting()));
        System.out.println("count : " + list.stream().count());

    }

}
