package cn.nihility.boot2.bean;

import cn.nihility.boot2.suports.AutoImportFactoryImpl01;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author dandelion
 * @date 2020:06:27 15:22
 */
//@SpringBootTest
public class FactoriesAutoImportTest {

    /*@Autowired
    private AutoImportFactoryImpl01 autoImportFactoryImpl01;

    @Test
    public void testFactoriesImportNotNull() {
        Assertions.assertNotNull(autoImportFactoryImpl01);
    }*/

    @Test
    public void testFactories() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(FactoriesAutoImportConfiguration.class);
        ctx.refresh();

        AutoImportFactoryImpl01 bean = ctx.getBean(AutoImportFactoryImpl01.class);
        bean.init("AutoImportFactoryImpl01");
    }

}
