package cn.nihility.jdk8;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * MethodReferenceDemo
 *
 * @author clover
 * @date 2020-01-04 16:30
 */
public class MethodReferenceDemo {

    public static void main(String[] args) {

        /*List<String> list = Arrays.asList("hello", "one", "two", "three", "four");
        list.forEach(item -> System.out.println(item)); // 通常方式
        System.out.println("==========================");
        list.forEach(System.out::println);*/

        Student s1 = new Student("zhangsan", 20);
        Student s2 = new Student("lishi", 10);
        Student s3 = new Student("wangwu", 30);
        Student s4 = new Student("zhaolu", 40);

        List<Student> list = Arrays.asList(s1, s2, s3, s4);

//        list.sort((t1, t2) -> t1.getAge() - t2.getAge());
//        list.forEach(it -> System.out.println(it.getName() + ": " + it.getAge()));

//        System.out.println("========================");
//        list.sort(Student::compareByAge);
//        list.forEach(it -> System.out.println(it.getName() + ": " + it.getAge()));

//        System.out.println("=========================");
//        list.sort(Student::compareByName);
//        list.forEach(it -> System.out.println(it.getName() + ": " + it.getAge()));


        Student.StudentComparator comparator = new Student.StudentComparator();
//        list.sort((t1, t2) -> comparator.compareByAge(t1, t2));
//        list.forEach(it -> System.out.println(it.getName() + ": " + it.getAge()));

//        System.out.println("=======================");
//        list.sort(comparator::compareByAge);
//        list.forEach(it -> System.out.println(it.getName() + ": " + it.getAge()));

//        list.sort(comparator::compareByName);
//        list.forEach(it -> System.out.println(it.getName() + ": " + it.getAge()));

//        list.sort(Student::compareWithAge);
//        list.forEach(it -> System.out.println(it.getName() + ": " + it.getAge()));

        List<String> cities = Arrays.asList("chongqing", "beijing", "shanghai", "qingdao");
        Collections.sort(cities, String::compareTo);
        cities.forEach(System.out::println);

        System.out.println("====================");
        MethodReferenceDemo demo = new MethodReferenceDemo();
        System.out.println(demo.getString(String::new));
        System.out.println(demo.getString2("hello", String::new));

    }

    public String getString(Supplier<String> supplier) {
        return supplier.get() + " test";
    }

    public String getString2(String str, Function<String, String> function) {
        return function.apply(str);
    }

}
