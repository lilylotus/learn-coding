package cn.nihility.jvm;

/**
 * ClassLoaderDemo
 *
 * @author clover
 * @date 2020-02-22 20:21
 */
public class ClassLoaderDemo {

    public static void main(String[] args) {

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        // sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println("system class loader " + systemClassLoader);

        Class<?> clazz = null;
        try {
            clazz = systemClassLoader.loadClass("cn.nihility.jvm.ClassLoaderTest");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(clazz);
        System.out.println(clazz.getClassLoader());
        // sun.misc.Launcher$AppClassLoader@18b4aac2


        /*ClassLoader classLoader = ClassLoaderDemo.class.getClassLoader();
        System.out.println("ClassLoaderDemo Class Loader : " + classLoader);
        // sun.misc.Launcher$AppClassLoader@18b4aac2

        ClassLoader parent = classLoader.getParent();
        System.out.println("parent Class Loader : " + parent);
        // sun.misc.Launcher$ExtClassLoader@5e2de80c

        ClassLoader parent1 = parent.getParent();
        System.out.println("Parent's Parent Class Loader : " + parent1);
        // null
        */

    }

}
