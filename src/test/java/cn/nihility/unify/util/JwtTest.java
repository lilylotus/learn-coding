package cn.nihility.unify.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    public void jwtTokenGenerateAndVerifyTest() {
        Map<String, String> params = new HashMap<>(4);
        params.put("USER_ID", "12345678");
        String token = JWTUtil.generateJwtToken(params);
        Assertions.assertNotNull(token);

        System.out.println(token);

        boolean verify = JWTUtil.verifyJwtToken(token, params);
        Assertions.assertTrue(verify);
    }

}
