package cn.nihility.jdk8.stream;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * StreamDemo11
 *
 * @author clover
 * @date 2020-01-06 09:00
 */
public class StreamDemo11 {

    public void test(Consumer<Integer> consumer) {

        if (consumer instanceof IntConsumer) {
            System.out.println("convert");
            ((IntConsumer) consumer).accept(1000);
        }

        consumer.accept(100);
    }

    public static void main(String[] args) {

        StreamDemo11 stream = new StreamDemo11();

        Consumer<Integer> consumer = i -> System.out.println(i);
        IntConsumer intConsumer = i -> System.out.println(i);

        stream.test(consumer);
        // stream.test(intConsumer); // 报错

        stream.test(consumer::accept);
        stream.test(intConsumer::accept);


    }

}
