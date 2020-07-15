package cn.nihility.jdk8.collector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * MyCollectors
 *
 * @author clover
 * @date 2020-01-05 11:58
 */
public class MyCollectors<T> implements Collector<T, Set<T>, Set<T>> {

    // 提供一个空的容器，供 accumulator 后续方法调用的容器
    @Override
    public Supplier<Set<T>> supplier() {
        System.out.println("supplier invoked!");
        return HashSet::new;
    }

    // void accept(T t, U u); T 为中间的结果容器， U 流中遍历的下一个元素
    @Override
    public BiConsumer<Set<T>, T> accumulator() {
        System.out.println("accumulator invoked!");
        // return HashSet<T>::add; 错误，有可能 supplier 返回 TreeSet 而 HashSet 不能满足要求
        // return (set, item) -> set.add(item); 和下面写法等价
        return Set::add;
    }

    // 将并行流的多个中间结果合并起来
    @Override
    public BinaryOperator<Set<T>> combiner() {
        System.out.println("combiner invoked!");
        return (set1, set2) -> { set1.addAll(set2); return set1; };
    }

    // 将所有的结果合并到一起
    @Override
    public Function<Set<T>, Set<T>> finisher() {
        System.out.println("finisher invoked!");
        // return t -> t;
        return Function.identity();
    }

    // 表示当前收集器诸多不可变的特性集合
    @Override
    public Set<Characteristics> characteristics() {
        System.out.println("characteristics invoked!");
        // Characteristics.IDENTITY_FINISH
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.UNORDERED));
    }

    public static void main(String[] args) {

        List<String> list = Arrays.asList("hello", "world", "welcome");

        Set<String> collect = list.stream().collect(new MyCollectors<>());
        System.out.println(collect);
    }
}
