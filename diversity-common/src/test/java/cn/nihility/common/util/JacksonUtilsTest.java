package cn.nihility.common.util;

import cn.nihility.common.pojo.UnifyResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JacksonUtilsTest {

    @Test
    void testJsonParse() {
        String body = "{\"code\":200,\"data\":{\"id\":\"randomId\",\"name\":\"randomName\"},\"message\":\"升级成功\"}";
        System.out.println(body);
        UnifyResult result = JacksonUtils.readJsonString(body, UnifyResult.class);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

}
