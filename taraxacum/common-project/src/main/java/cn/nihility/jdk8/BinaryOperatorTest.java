package cn.nihility.jdk8;

import java.util.Comparator;
import java.util.function.BinaryOperator;

/**
 * BinaryOperatorTest
 *
 * @author clover
 * @date 2020-01-04 15:10
 */
public class BinaryOperatorTest {

    public static void main(String[] args) {

        BinaryOperatorTest test = new BinaryOperatorTest();

        System.out.println(test.compute(10, 20, (a, b) -> a + b));
        System.out.println(test.compute(10, 20, (a, b) -> a * b));
        System.out.println(test.compute(10, 20, (a, b) -> a - b));
        System.out.println(test.compute(10, 20, (a, b) -> a / b));


        System.out.println("==================================");
        System.out.println(test.getShort("Hello", "wc", Comparator.comparingInt(String::length)));
        System.out.println(test.getShort("Hello", "wc", Comparator.comparingInt(a -> a.charAt(0))));


    }


    public int compute(int a, int b, BinaryOperator<Integer> function) {
        return function.apply(a, b);
    }

    public String getShort(String a, String b, Comparator<String> comparator) { return BinaryOperator.minBy(comparator).apply(a, b); }

}
