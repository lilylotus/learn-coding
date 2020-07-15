package cn.nihility.jdk8;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * FunctionTest
 *
 * @author clover
 * @date 2020-01-04 12:09
 */
public class FunctionTest {

    public static void main(String[] args) {

        /**
         *  String::toUpperCase -> 这是实例方法引用，此方法的第一个输入一定是存在本调用实例
         *  lambda 调用传入的一个参数一定是此被调用对象的实例
         *
         */

        Function<String, String> function = String::toUpperCase;
        System.out.println(function.getClass().getInterfaces()[0]);
        System.out.println("====================");


        List<String> nameList = Arrays.asList("zhangsna", "lishi", "wangwu", "zhaoliu");
        /*Collections.sort(nameList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                //return o2.compareTo(o1);
                return o1.compareTo(o2);
            }
        });*/

        // Collections.sort( nameList,  (o1, o2) -> o2.compareTo(o1) );

        Collections.sort( nameList, Comparator.reverseOrder());

        System.out.println(nameList);
        System.out.println("======================");

        FunctionTest functionTest = new FunctionTest();
        System.out.println(functionTest.compute(1, (value -> value * 2) ));
        System.out.println(functionTest.compute(10, (value -> 10 + value)));
        System.out.println(functionTest.convert(20, item -> item + " + Hello Lambda."));

        System.out.println("==============================");


    }

    public int compute(int a, Function<Integer, Integer> function) {
        Integer result = function.apply(a);
        return result;
    }

    public String convert(int a, Function<Integer, String> function) {
        return function.apply(a);
    }

}
