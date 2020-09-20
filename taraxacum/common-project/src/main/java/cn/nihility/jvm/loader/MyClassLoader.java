package cn.nihility.jvm.loader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * MyClassLoader
 *
 * @author clover
 * @date 2020-02-22 20:17
 */
public class MyClassLoader extends ClassLoader {

    private String name;

    public MyClassLoader(ClassLoader parent, String name) {
        super(parent);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("loadClass name : " + name);
        Class<?> clazz = findClass(name);
        System.out.println("loadClass in findClass clazz -> " + clazz);
        if (clazz != null) { return clazz; }
        return getSystemClassLoader().loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) {
        byte[] data = getClassByteArray(name);
        if (null != data) {
            return this.defineClass(name, data, 0, data.length);
        } else {
            return null;
        }
    }

    private byte[] getClassByteArray(String name) {
        // C:\programming\idea\future\practice\out\production\classes D:/temporal/java/
//        String clazzPath = "C:\\programming\\idea\\future\\practice\\out\\production\\classes/" + name.replaceAll("\\.", "/") + ".class";
        String clazzPath = "/home/dandelion/temporary/java/" + name.replaceAll("\\.", "/") + ".class";

//        String clazzPath = "C:\\programming\\idea\\future\\practice\\out\\production\\classes\\cn/nihility/jvm/ClassInstance.class";
        System.out.println("Class Path : " + clazzPath);

        FileInputStream fis = null;
        ByteArrayOutputStream bao = null;
        try {
            File file = new File(clazzPath);
            if (!file.exists()) { return null; }
            fis = new FileInputStream(clazzPath);
            bao = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = fis.read(buffer)) != -1) {
                bao.write(buffer, 0, len);
            }

            return bao.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bao != null) {
                try {
                    bao.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    static class AutoCompileAndExecuteThread extends Thread {
        private String clazzName;
        public AutoCompileAndExecuteThread(String clazzName) {
            this.clazzName = clazzName;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String cmdLine = "javac -d /home/dandelion/Documents/temporary/java/ -encoding UTF-8 /home/dandelion/Documents/temporary/java/*.java";
                    String[] cmd = {"/bin/bash", "-c", cmdLine};
                    Runtime.getRuntime().exec(cmd);
                    System.out.println("exec command");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MyClassLoader loader = new MyClassLoader(getSystemClassLoader(), currentThread().getName());
                try {
                    Class<?> clazz = loader.loadClass(clazzName);

                    if (null != clazz) {
                        System.out.println(clazz.getClassLoader());
                        Object instance = clazz.newInstance();
                        Method method = clazz.getDeclaredMethod("say");
                        method.setAccessible(true);
                        method.invoke(instance);
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("==========================");
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        // 使用自定义类加载器，实现打破双亲委派机制
        ClassLoader classLoader = MyClassLoader.class.getClassLoader();
        System.out.println(classLoader);
        MyClassLoader loader = new MyClassLoader(classLoader, "MyClassLoader");

        System.out.println("----------------------");
        Class<?> clazz = loader.loadClass("cn.nihility.jvm.ClassInstance");
        Optional<Class<?>> op = Optional.ofNullable(clazz);
        op.ifPresent(cz -> { System.out.println(cz);
            System.out.println(cz.getClassLoader()); });

        System.out.println("===============");
        try {

            Object instance = clazz.newInstance();
            Method sayMethod = clazz.getDeclaredMethod("say");
            sayMethod.setAccessible(true);
            sayMethod.invoke(instance);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("-----------------");
        Class<?> clazz1 = getSystemClassLoader().loadClass("cn.nihility.jvm.ClassInstance");
        System.out.println(clazz1 + " classLoader " + clazz1.getClassLoader());

        try {
            Object instance = clazz1.newInstance();
            Method sayMethod = clazz1.getDeclaredMethod("say");
            sayMethod.setAccessible(true);
            sayMethod.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("==========================");
        System.out.println(clazz.getName() + " : " + clazz.getClassLoader());
        System.out.println(clazz1.getName() + " : " + clazz1.getClassLoader());

        /*AutoCompileAndExecuteThread ac = new AutoCompileAndExecuteThread("cn.nihility.jvm.ClassInstance");
        ac.start();*/

        /*String cmdLine = "javac -d /home/dandelion/Documents/temporary/java -encoding UTF-8 /home/dandelion/Documents/temporary/java/*.java";
//        String cmd = "bash /home/dandelion/Documents/temporary/java/compile.sh";
//        String cmd = "ls /";
        String[] cmd = {"/bin/bash", "-c", cmdLine};
        try {
            Process exec = Runtime.getRuntime().exec(cmd);
            exec.waitFor();
            InputStream inputStream = exec.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public static void main1(String[] args) {

        ClassLoader classLoader = MyClassLoader.class.getClassLoader();
        System.out.println(classLoader);
        MyClassLoader loader = new MyClassLoader(null, "MyClassLoader");
        MyClassLoader loader2 = new MyClassLoader(classLoader, "MyClassLoader");

        try {
            Class<?> clazz = loader.loadClass("cn.nihility.jvm.ClassInstance");
//            Class<?> clazz2 = loader.loadClass("cn.nihility.jvm.ClassInstance");
//            Class<?> clazz2 = loader2.loadClass("cn.nihility.jvm.ClassInstance");

//            System.out.println("class1 equal class2 " + (clazz == clazz2));

            if (clazz != null) {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                for (Method method : declaredMethods) {
                    System.out.println(method.getName() + " : " + method.getReturnType().getName() + " : " + Modifier.toString(method.getModifiers()));
                }
                System.out.println("======================");

                Method method = clazz.getMethod("say");
                System.out.println(method.getName() + " : " + method.getReturnType().getName() + " : " + Modifier.toString(method.getModifiers()));
                System.out.println("-----------------------");

                Object instance = clazz.newInstance();
                method.invoke(instance);

                Method say2 = clazz.getDeclaredMethod("say2");
                say2.setAccessible(true);
                say2.invoke(instance);

            }
            System.out.println("-----------------------------");
            System.out.println(clazz);
            System.out.println(clazz.getClassLoader());
            System.out.println(clazz.getClassLoader().getParent());


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }
}
