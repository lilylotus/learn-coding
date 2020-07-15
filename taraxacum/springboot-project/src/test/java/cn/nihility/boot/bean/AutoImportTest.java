package cn.nihility.boot.bean;

import static org.junit.jupiter.api.Assertions.*;

import cn.nihility.boot.suports.AutoImportFactoryImpl02;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author dandelion
 * @date 2020:06:27 17:45
 */
@SpringBootTest
public class AutoImportTest {

    @Autowired
    private AutoImportFactoryImpl02 autoImportFactoryImpl02;

    @Test
    public void testImportNotNull() {
        assertNotNull(autoImportFactoryImpl02);
    }

}
