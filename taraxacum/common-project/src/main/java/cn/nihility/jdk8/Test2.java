package cn.nihility.jdk8;

/**
 * Test2
 *
 * @author clover
 * @date 2020-01-04 11:14
 */
public class Test2 {

    public static void main(String[] args) {

        Test2 test2 = new Test2();

        test2.myTest( () -> System.out.println("My Interface Test") );
        System.out.println("=======================");


        MyInterface2 myInterface2 = () -> System.out.println("My Interface 2");
        myInterface2.test();

        System.out.println(myInterface2.getClass());
        System.out.println(myInterface2.getClass().getSuperclass());
        System.out.println(myInterface2.getClass().getInterfaces()[0]);
        System.out.println("=======================");

    }

    public void myTest(MyInterface2 interface2) {
        System.out.println("1");
        interface2.test();
        System.out.println("2");
    }

}

interface MyInterface2 {

    void test();

    String toString();

}
