package cn.nihility.jvm.loader;

import java.io.*;

/**
 * 自定义本地的 ClassLoader
 * 不去重载 ${@link #loadClass(String)} 就会按照双亲委派机制去加载类，不会走自定义的类加载机制
 *
 * @author dandelion
 * @date 2020-09-20 14:32
 */
public class LocalClassLoader extends ClassLoader {

    // 默认加载的路径
    private String prefixPath;
    // 当前 ClassLoader 的名称
    private String classLoaderName;

    private static final String FILE_EXTENSION = ".class";

    public LocalClassLoader(ClassLoader parent, String prefixPath, String classLoaderName) {
        super(parent);
        this.prefixPath = prefixPath;
        this.classLoaderName = classLoaderName;
    }

    public LocalClassLoader(String prefixPath, String classLoaderName) {
        this.prefixPath = prefixPath;
        this.classLoaderName = classLoaderName;
    }

    /*@Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("load class: " + name);
        Class<?> clazz = findClass(name);
        System.out.println("find class: " + name + ", clazz : " + clazz);
        if (null == clazz) {
            System.out.println("find class use system class loader");
            clazz = getSystemClassLoader().loadClass(name);
        }
        System.out.println("--------------------");
        return clazz;
    }*/

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        byte[] data = loadClassData(name);
        if (null != data) {
            System.out.println("define class: " + name);
            clazz = defineClass(name, data, 0, data.length);
        }
        System.out.println("find class: " + name + ", clazz: " + clazz);
        return clazz;
    }

    private byte[] loadClassData(String clazzName) {
        String classPath = prefixPath + clazzName.replace(".", "/") + FILE_EXTENSION;
        System.out.println("load local class data file: " + classPath);
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            File classFile = new File(classPath);
            if (!classFile.exists()) {
                System.out.println("class file not found: " + classPath);
                return null;
            }
            is = new FileInputStream(classFile);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int readSize;
            while ((readSize = is.read(buffer)) > 0) {
                baos.write(buffer, 0, readSize);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "[" + this.classLoaderName + ":" + this.hashCode() + "]";
    }
}
