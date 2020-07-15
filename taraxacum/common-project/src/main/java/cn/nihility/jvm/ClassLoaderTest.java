package cn.nihility.jvm;

/**
 * ClassLoaderTest
 *
 * @author clover
 * @date 2020-02-22 20:52
 */
public class ClassLoaderTest {

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.class.path"));
        System.out.println(System.getProperty("java.ext.dirs"));
    }
}
