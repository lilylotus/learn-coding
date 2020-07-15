package cn.nihility.jdk8.function;

import org.apache.commons.lang3.RandomUtils;

import java.util.Comparator;
import java.util.function.*;

/**
 * FunctionTest
 *
 * @author clover
 * @date 2020-02-26 17:15
 */
public class FunctionTest {

    public static void main(String[] args) {

        Integer v1 = 30, v2 = 40;
        Double d1 = 30.0, d2 = 40.0;

        FunctionTest functionTest = new FunctionTest();

        functionTest.biConsumerTest(v1, v2, (t, u) -> {
            System.out.println("BiConsumer accept using " + (t + u));
        });

        System.out.println("BiFunction Test + " + functionTest.biFunctionTest(v1, v2, ((t, u) -> t * u)));

        System.out.println("BinaryOperator Test + " + functionTest.binaryOperator(v1, v2, ((t, u) -> t * u)));

        System.out.println("BinaryOperator MinBy Test + " +
                functionTest.binaryOperatorMinBy(v1, v2, BinaryOperator.minBy(Comparator.comparingInt(a -> a))));


        System.out.println("===============================");
        System.out.println("BiPredicate " + functionTest.biPredicate(v1, v2, ((a , b) -> a < b)));
        /* a > b and b < 40 */
        System.out.println("BiPredicate And " + functionTest.biPredicateAnd(v1, v2, (a , b) -> a < b, (a, b) -> a < 40));

        System.out.println("===============================");
        System.out.println("Boolean.FALSE -> " + functionTest.booleanSupplier(() -> false));

        System.out.println("===============================");
        functionTest.consumer(v1, a -> System.out.println("Consumer double a " + a * a));

        System.out.println("===============================");
        System.out.println("DoubleBinaryOperator -> " + functionTest.doubleBinaryOperator(d1, d2, (a, b) -> a * b / 2.0));

        System.out.println("===============================");
        System.out.println("supplier = " + functionTest.supplier(() -> RandomUtils.nextInt()));

    }

    public Integer supplier(Supplier<Integer> supplier) {
        return supplier.get();
    }

    public double doubleBinaryOperator(double d1, double d2, DoubleBinaryOperator operator) {
        return operator.applyAsDouble(d1, d2);
    }

    public void consumer(Integer v1, Consumer<Integer> consumer) {
        consumer.accept(v1);
    }

    public void biConsumerTest(int a, int b, BiConsumer<Integer, Integer> biConsumer) {
        System.out.println("BiConsumer accept");
        biConsumer.accept(a, b);
        System.out.println("BiConsumer End");
    }

    public Integer biFunctionTest(Integer v1, Integer v2, BiFunction<Integer, Integer, Integer> biFunction) {
        return biFunction.apply(v1, v2);
    }

    public Integer binaryOperator(Integer v1, Integer v2, BinaryOperator<Integer> operator) {
        return operator.apply(v1, v2);
    }

    public Integer binaryOperatorMinBy(Integer v1, Integer v2, BinaryOperator<Integer> operator) {
        return operator.apply(v1, v2);
    }

    public boolean biPredicate(Integer v1, Integer v2, BiPredicate<Integer, Integer> predicate) {
        return predicate.test(v1, v2);
    }

    public boolean biPredicateAnd(Integer v1, Integer v2,
                                  BiPredicate<Integer, Integer> predicate,
                                  BiPredicate<Integer, Integer> and) {
        return predicate.and(and).test(v1, v2);
    }

    public Boolean booleanSupplier(BooleanSupplier supplier) {
        return supplier.getAsBoolean();
    }

}
