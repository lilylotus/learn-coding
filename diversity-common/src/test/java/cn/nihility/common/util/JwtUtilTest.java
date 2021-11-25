package cn.nihility.common.util;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void testCreateJwtToken() {

        Map<String, String> cliams = new HashMap<>();
        cliams.put("userId", "uid");
        cliams.put("userName", "name");

        Algorithm al = Algorithm.HMAC256("1234567890");

        String jwt = JwtUtil.createJwtToken("jti-Id", 5000L, cliams);
        String jwt2 = JwtUtil.createJwtToken("jti-Id", 5000L, cliams, Algorithm.HMAC256("12345678901111"));

        Assertions.assertNotNull(jwt);

        /*try {
            Thread.sleep(1 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //JwtUtil.verifyJwtToken(jwt2);

        String userId = JwtUtil.obtainJwtClaim("userId", jwt);
        Assertions.assertEquals("uid", userId);

        Map<String, String> claims = JwtUtil.obtainJwtClaims(jwt);
        System.out.println(claims);

        String is = JwtUtil.jwtDecodeClaim("userId1", jwt);
        Assertions.assertEquals("uid", is);

    }

    @Test
    void jwtTest() {
        String jwt = JwtUtil.createJwtToken("jwt-id", null);
        DecodedJWT decodedJWT = JwtUtil.verifyJwtToken(jwt);
        Assertions.assertNotNull(decodedJWT);
    }

}
