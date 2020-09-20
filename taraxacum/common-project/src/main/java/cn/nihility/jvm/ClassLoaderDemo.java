package cn.nihility.jvm;

import cn.nihility.jvm.loader.LocalClassLoader;

import java.lang.reflect.InvocationTargetException;

/**
 * 1. 同一个实例类加载器多次加载同一个类会出现异常：
 * java.lang.LinkageError: loader (instance of  cn/nihility/jvm/loader/LocalClassLoader): attempted  duplicate class definition for name: "cn/nihility/jvm/ClassInstance"
 *
 * 2. 不同实例的类加载器之间存在命名空间隔离，因此不用实例类加载器加载的类实例不能相互反射调用
 *
 * @author clover
 * @date 2020-02-22 20:21
 * +
 *
 */
public class ClassLoaderDemo {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        // sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println("system class loader: " + systemClassLoader);

        String prefix = "/dandelion/programming/learn-coding/taraxacum/common-project/out/";
        String classLoaderName = "LocalClassLoader";
        String className = "cn.nihility.jvm.ClassInstance";

//        LocalClassLoader loader = new LocalClassLoader(systemClassLoader, prefix, classLoaderName);
        LocalClassLoader loader = new LocalClassLoader(prefix, classLoaderName);
        Class<?> clazz = loader.loadClass(className);
        System.out.println("clazz: " + clazz + ", classLoader: " + clazz.getClassLoader());
        System.out.println("clazz: " + ClassLoaderDemo.class + ", classLoader: " + ClassLoaderDemo.class.getClassLoader());

        /*Method method = clazz.getDeclaredMethod("sayInstance");
        Object instance = clazz.newInstance();
        method.invoke(instance);*/

        System.out.println("======================================");
        LocalClassLoader loader1 = new LocalClassLoader(prefix, classLoaderName);
        Class<?> clazz1 = loader1.loadClass(className);
        System.out.println("clazz: " + clazz1 + ", classLoader: " + clazz1.getClassLoader());

        System.out.println("======================================");
        System.out.println(clazz1 == clazz);

        System.out.println("======================================");
        // method.invoke(instance1); 不同类加载器实例加载的实例化类之间反射不能相互处理


        /*Class<?> clazz = null;
        try {
            clazz = systemClassLoader.loadClass("cn.nihility.jvm.loader.ClassLoaderDefaultPath");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(clazz);
        System.out.println(clazz.getClassLoader());*/
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
