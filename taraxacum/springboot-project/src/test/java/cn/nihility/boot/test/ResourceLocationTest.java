package cn.nihility.boot.test;

import cn.nihility.boot.util.ResourceUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ResourceLocationTest {

    @Test
    void testLocationFiles() {
        final List<Class<?>> classes = ResourceUtil.scanPackageClass("cn.nihility.boot", 2);
        classes.forEach(clazz -> System.out.println(clazz.getName()));
    }

}
