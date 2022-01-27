package cn.nihility.profile.binder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

/**
 * @author orchid
 * @date 2022-01-27 21:20:35
 */
@SpringBootTest
class BinderTest {

    @Autowired
    private Environment environment;

    @Test
    void testBinderList() {
        List<String> listResult = Binder.get(environment).bind("binder.list", Bindable.listOf(String.class))
            .get();
        Assertions.assertNotNull(listResult);
        Assertions.assertEquals(3, listResult.size());
        Assertions.assertEquals("a", listResult.get(0));
    }

    @Test
    void testBinderMap() {
        Map<String, String> mapBindResult = Binder.get(environment)
            .bind("binder.map", Bindable.mapOf(String.class, String.class)).get();
        Assertions.assertNotNull(mapBindResult);
        Assertions.assertEquals(3, mapBindResult.size());
        Assertions.assertEquals("value1", mapBindResult.get("key1"));
    }


    @Test
    void testBinderWithBean() {
        BinderBean bean = Binder.get(environment).bind("binder.map", Bindable.of(BinderBean.class)).get();
        Assertions.assertNotNull(bean);
    }

}
