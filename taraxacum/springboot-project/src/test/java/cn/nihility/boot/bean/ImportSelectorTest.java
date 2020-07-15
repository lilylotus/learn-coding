package cn.nihility.boot.bean;

import cn.nihility.boot.selector.Cat;
import cn.nihility.boot.selector.Zoom;
import cn.nihility.boot.selector.bean.BeanImportSelectorConfiguration;
import cn.nihility.boot.suports.AutoImportFactoryImpl01;
import cn.nihility.boot.suports.AutoImportFactoryImpl02;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author dandelion
 * @date 2020:06:27 17:23
 */
@SpringBootTest
public class ImportSelectorTest {

    @Autowired
    private Cat cat;
    @Autowired
    private BeanImportSelectorConfiguration beanImportSelectorConfiguration;
    @Autowired
    private Zoom zoom;

    @Autowired
    private AutoImportFactoryImpl02 autoImportFactoryImpl02;
    @Autowired
    private AutoImportFactoryImpl01 autoImportFactoryImpl01;
    /*@Autowired
    @Qualifier("autoImportFactoryImpl01")
    private IAutoImportFactory autoImportFactory;*/

    @Test
    public void testImportSelectorBeanNotNull() {
        Assertions.assertNotNull(cat);
        Assertions.assertNotNull(beanImportSelectorConfiguration);
        Assertions.assertNotNull(zoom);

        assertEquals(10, cat.getAge());
        assertEquals("ImportSelectorCat", cat.getName());

        System.out.println("cat " + cat.hashCode());
        System.out.println("cat1 " + zoom.getCat1().hashCode());
        System.out.println("cat2 " + zoom.getCat2().hashCode());

    }

    @Test
    public void testImportNotNull() {
        assertNotNull(autoImportFactoryImpl02);
        assertNotNull(autoImportFactoryImpl01);
//        assertNotNull(autoImportFactory);
    }

}
