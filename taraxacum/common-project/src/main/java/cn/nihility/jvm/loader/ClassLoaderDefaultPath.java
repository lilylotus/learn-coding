package cn.nihility.jvm.loader;

/**
 * ClassLoader 默认的加载路劲
 *
 * @author clover
 * @date 2020-02-22 20:52
 */
public class ClassLoaderDefaultPath {

    public static void main(String[] args) {
        System.out.println("BootStrapClassLoader path: " + System.getProperty("sun.boot.class.path"));
        System.out.println("ExtClassLoader path: " + System.getProperty("java.ext.dirs"));
        System.out.println("ApplicationClassLoader path: " + System.getProperty("java.class.path"));

        // file:/taraxacum/common-project/out/production/classes/
        System.out.println(ClassLoaderDefaultPath.class.getClassLoader().getResource(""));
        // null
        System.out.println(ClassLoaderDefaultPath.class.getClassLoader().getResource("/"));
        // file:/taraxacum/common-project/out/production/classes/cn/nihility/jvm/loader/
        System.out.println(ClassLoaderDefaultPath.class.getResource(""));
        // file:/taraxacum/common-project/out/production/classes/
        System.out.println(ClassLoaderDefaultPath.class.getResource("/"));
    }

}
