package cn.nihility.boot.test;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author dandelion
 * @date 2020:06:26 19:25
 */
public class FactoriesTest {

    @Test
    public void testListToArray() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");

        String[] strings = list.toArray(new String[0]);

        System.out.println(strings[0]);
        System.out.println(strings[1]);
    }

    @Test
    public void testSpringFactories() {
        ClassLoader classLoader = FactoriesTest.class.getClassLoader();
        String resource = "META-INF/spring.factories";

        try {
            Enumeration<URL> resources = classLoader.getResources(resource);

            if (null != resources) {
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    System.out.println("==========" + url);

                    // cleanPath(url);

                    URLConnection urlConnection = url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();

                    Properties properties = new Properties();
                    properties.load(inputStream);
                    properties.forEach((key, value) -> System.out.println("key [" + key + "] value [" + value + "]"));

                    if (null != inputStream) { inputStream.close(); }
                }
            }

            System.out.println("===============================");
            Enumeration<URL> systemResources = ClassLoader.getSystemResources("");
            if (systemResources != null) {
                while (systemResources.hasMoreElements()) {
                    URL url = systemResources.nextElement();
                    System.out.println(url);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String cleanPath(URL url) {
        if (url == null) {
            return null;
        } else {
            String path = url.getPath();
            String prefix = "";
            int filePreFixIndex = path.indexOf(":");
            System.out.println("file index = " + filePreFixIndex);
            if (filePreFixIndex != -1) {
                prefix = path.substring(0, filePreFixIndex + 1);
                path = path.substring(filePreFixIndex + 1);
            }
            File file = new File(path);
            System.out.println("exists " + file.exists() + " prefix [" + prefix + "] path : " + path);
            return path;
        }
    }

}
