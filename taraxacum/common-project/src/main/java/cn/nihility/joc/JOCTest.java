package cn.nihility.joc;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

/**
 * JOCTest
 *
 * @author dandelion
 * @date 2020-05-11 14:12
 */
public class JOCTest {

    public static void main(String[] args) {
        System.out.println(VM.current().details());
        System.out.println(ClassLayout.parseClass(A.class).toPrintable());

        A a = new A();
        System.out.println(ClassLayout.parseInstance(a).toPrintable());
    }

}
