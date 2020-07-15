package cn.nihility.learn.bean;

import cn.nihility.learn.config.AutoImportBeanConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AutoImportBeanTest
 *
 * @author dandelion
 * @date 2020-03-24 17:00
 */
@SpringBootTest
class AutoImportBeanTest {

    /*@Autowired
    AutoImportBean autoImportBean;

    @Autowired
    AutoImportBeanConfig autoImportBeanConfig;*/

   @Autowired
    AutoImportBeanConfiguration autoImportBeanConfiguration;


    @Test
    void testAutoImportBean() {
        assertNotNull(autoImportBeanConfiguration);
        System.out.println(autoImportBeanConfiguration);

        /*assertNotNull(autoImportBean);
        System.out.println(autoImportBean);

        assertNotNull(autoImportBeanConfig);
        System.out.println(autoImportBeanConfig);*/
    }

}