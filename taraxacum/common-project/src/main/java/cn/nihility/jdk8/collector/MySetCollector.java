package cn.nihility.jdk8.collector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * MySetCollector
 *
 * @author clover
 * @date 2020-01-05 13:05
 */
public class MySetCollector<T> implements Collector<T, Set<T>, Map<T, T>> {
    @Override
    public Supplier<Set<T>> supplier() {
        System.out.println("supplier invoked!");
        //return HashSet::new;
        return () -> {
            System.out.println("supplier invoked! + ");
            return new HashSet<>();
        };
    }

    @Override
    public BiConsumer<Set<T>, T> accumulator() {
        System.out.println("accumulator invoked!");
        // 打印 set 就会在并行操作式后报出异常
        return (set, item) -> { set.add(item); System.out.println("accumulator invoked! + set " + set + " - " + Thread.currentThread().getName()); };
    }

    @Override
    public BinaryOperator<Set<T>> combiner() {
        System.out.println("combiner invoked!");
        return (set1, set2) -> { set1.addAll(set2); System.out.println("set1 : " + set1 + " - set2 : " + set2); return set1; };
    }

    @Override
    public Function<Set<T>, Map<T, T>> finisher() {
        System.out.println("finisher invoked!");

        return set -> {
            Map<T, T> map = new HashMap<>();
            set.stream().forEach( item -> map.put(item, item));
            return map;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        System.out.println("characteristics invoked!");
        // Characteristics.IDENTITY_FINISH 异常，不会执行 finisher， Set<String> 不能直接转换为 Map<String, String>
        // , Characteristics.CONCURRENT
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.UNORDERED));
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {

            List<String> list = Arrays.asList("hello", "world", "hello world", "a", "b", "c", "d");
            Set<String> set = new HashSet<>();
            set.addAll(list);
            System.out.println("set : " + set);

            Map<String, String> map = set.parallelStream().collect(new MySetCollector<>());
            System.out.println(map);
        }
    }

}
