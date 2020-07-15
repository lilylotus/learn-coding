package cn.nihility.jdk8;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * FunctionTest2
 *
 * @author clover
 * @date 2020-01-04 12:54
 */
public class FunctionTest2 {

    public static void main(String[] args) {

        FunctionTest2 test = new FunctionTest2();

        int v1 = test.computeCompose(10, v -> v * 3, v -> v * v);
        int v2 = test.computeAndThen(10, v -> v * 3, v -> v * v);
        System.out.println("compose " + v1);
        System.out.println("andThen " + v2);

        System.out.println("=========================");

        System.out.println(test.computeBiFunction(10, 10, (value1, value2) -> value1 - value2));
        System.out.println(test.computeBiFunction(10, 10, (value1, value2) -> value1 + value2));
        System.out.println(test.computeBiFunction(10, 10, (value1, value2) -> value1 * value2));
        System.out.println(test.computeBiFunction(10, 10, (value1, value2) -> value1 / value2));

        System.out.println("===========================");

        System.out.println(test.computeBiFunctionAndThen(10, 10, (value1, value2) -> value1 + value2, value -> value * value));

    }


    public int computeCompose(int a, Function<Integer, Integer> function1, Function<Integer, Integer> function2) {
        return function1.compose(function2).compose(function2).andThen(function2).apply(a);
    }

    public int computeAndThen(int a, Function<Integer, Integer> function1, Function<Integer, Integer> function2) {
        return function1.andThen(function2).apply(a);
    }

    public int computeBiFunction(int a, int b, BiFunction<Integer, Integer, Integer> biFunction) {
        return biFunction.apply(a, b);
    }

    public int computeBiFunctionAndThen(int a, int b, BiFunction<Integer, Integer, Integer> biFunction,
                                        Function<Integer, Integer> function) {
        return biFunction.andThen(function).apply(a, b);
    }

    public int add(int a, int b) {
        return a + b;
    }

    public int subtraction(int a, int b) {
        return a - b;
    }

}
