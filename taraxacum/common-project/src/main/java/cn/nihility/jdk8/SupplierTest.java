package cn.nihility.jdk8;

import java.util.function.Supplier;

/**
 * SupplierTest
 *
 * @author clover
 * @date 2020-01-04 14:59
 */
public class SupplierTest {

    public static void main(String[] args) {
        Supplier<String> supplier = () -> "Hello Supplier";
        System.out.println(supplier.get());

        System.out.println("=================");

        Supplier<FunctionPerson> supplier1 = () -> new FunctionPerson("Supplier", 20);
        System.out.println(supplier1.get());

        System.out.println("================");
        // 构造方法引用
        Supplier<FunctionPerson> supplier2 = FunctionPerson::new;
        System.out.println(supplier2.get());

    }

}
