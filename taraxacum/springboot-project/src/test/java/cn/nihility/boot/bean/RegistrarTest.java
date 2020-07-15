package cn.nihility.boot.bean;

import cn.nihility.boot.registrar.BeanImportBeanDefinitionRegistrar;
import cn.nihility.boot.registrar.dto.Duck;
import cn.nihility.boot.registrar.mapper.DuckDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author dandelion
 * @date 2020:06:27 19:12
 */
@SpringBootTest
public class RegistrarTest {

    @Autowired
    private DuckDao duckDao;

    @Test
    public void testRegistrarBeanNotNull() {
        Assertions.assertNotNull(duckDao);
        Duck duck = duckDao.findDuckById(1);
        System.out.println(duck);

        BeanFactory beanFactory = BeanImportBeanDefinitionRegistrar.getBeanFactory();
        DuckDao bean = beanFactory.getBean(DuckDao.class);

    }

}
