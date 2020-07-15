package cn.nihility.boot.bean;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author dandelion
 * @date 2020:06:26 17:23
 */
@SpringBootTest
public class InjectBeanTest {

    @Autowired
    private InjectBeanDemo injectBeanDemo;
    @Autowired
    private InjectBeanDemo2 injectBeanDemo2;

    @Autowired
    private InjectBeanDemo2.InnerClass innerClass;
    @Autowired
    private InjectBeanDemo2.InnerPropertyClass innerPropertyClass;

    @Autowired
    private InjectConfiguration injectConfiguration;

    @Test
    public void testBeanInjectProperties() {
        assertEquals(innerClass.getName(), "Inject Property Name");
        assertEquals(innerClass.getAge(), 2000);

        assertEquals(innerPropertyClass.getName(), "小米");
        assertEquals(innerPropertyClass.getAge(), 10);

        assertEquals(injectBeanDemo2.getAge(), 10);
        assertEquals(injectBeanDemo2.getName(), "小米");
    }

    @Test
    public void testInjectConfiguration() {
        assertEquals(injectConfiguration.getFirstName(), "It's first name.");
        assertEquals(injectConfiguration.getLastName(), "It's last name.");
        assertEquals(injectConfiguration.getFullName(), "It's full name.");
        assertEquals(injectConfiguration.getAddress(), "192.168.1.100");

        System.out.println("====================");
        List<InjectConfiguration.MyPojo> pojoList = injectConfiguration.getPojoList();
        pojoList.forEach(System.out::println);
        System.out.println("====================");
        List<String> servers = injectConfiguration.getServers();
        servers.forEach(System.out::println);

    }

}
