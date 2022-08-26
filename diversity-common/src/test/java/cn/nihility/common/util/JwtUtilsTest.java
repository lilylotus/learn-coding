package cn.nihility.common.util;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class JwtUtilsTest {

    @Test
    void testCreateJwtToken() {

        Map<String, String> cliams = new HashMap<>(8);
        cliams.put("userId", "uid");
        cliams.put("userName", "name");

        Algorithm al = Algorithm.HMAC256("1234567890");

        String jwt = JwtUtils.createJwtToken("jti-Id", 5000L, cliams).getAccessToken();
        String jwt2 = JwtUtils.createJwtToken("jti-Id", 5000L, cliams, Algorithm.HMAC256("12345678901111")).getAccessToken();

        Assertions.assertNotNull(jwt);

        /*try {
            Thread.sleep(1 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //JwtUtil.verifyJwtToken(jwt2);

        String userId = JwtUtils.obtainJwtClaim(jwt, "userId");
        Assertions.assertEquals("uid", userId);

        Map<String, String> claims = JwtUtils.obtainJwtClaims(jwt);
        System.out.println(claims);

        String is = JwtUtils.jwtDecodeClaim(jwt, "userId");
        Assertions.assertEquals("uid", is);

    }

    @Test
    void jwtTest() {
        String jwt = JwtUtils.createJwtToken("jwt-id", null).getAccessToken();
        DecodedJWT decodedJWT = JwtUtils.verifyJwtToken(jwt);
        Assertions.assertNotNull(decodedJWT);
    }

}
