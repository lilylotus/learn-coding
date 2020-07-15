package cn.nihility.jdk8;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * PredicateTest
 *
 * @author clover
 * @date 2020-01-04 14:44
 */
public class PredicateTest {

    public static void main(String[] args) {


        Predicate<String> predicate = p -> p.length() > 3;
        Predicate<String> predicate1 = p -> p.length() < 5;
        System.out.println(predicate.negate().test("Hello"));

        System.out.println(predicate.and(predicate1).test("Hell"));
        System.out.println(predicate.or(predicate1).test("Hello"));

        System.out.println("==============================");
        PredicateTest test = new PredicateTest();
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        test.conditionFilter(list, item -> item % 2 == 0);

        System.out.println("==============================");
        test.conditionFilter(list, item -> item > 5);

        System.out.println("==============================");
        test.conditionFilter(list, item -> true);

        System.out.println("==============================");
        test.conditionFilter(list, item -> false);
    }

    public void conditionFilter(List<Integer> list, Predicate<Integer> predicate) {
        for (Integer integer : list) {
            if (predicate.test(integer)) {
                System.out.println(integer);
            }
        }
    }

}
