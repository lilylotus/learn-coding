package cn.nihility.use.bean;

import cn.nihility.learn.bean.AutoImportBean;
import cn.nihility.learn.bean.AutoImportBeanConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * AutoStarterBeanTest
 *
 * @author dandelion
 * @date 2020-03-24 17:55
 */
@SpringBootTest
public class AutoStarterBeanTest {

    @Autowired
    AutoImportBean autoImportBean;
    @Autowired
    AutoImportBeanConfig autoImportBeanConfig;

    @Test
    void testAutoStarter() {
        Assertions.assertNotNull(autoImportBean);
        System.out.println(autoImportBean);

        Assertions.assertNotNull(autoImportBeanConfig);
        System.out.println(autoImportBeanConfig);
    }
}
