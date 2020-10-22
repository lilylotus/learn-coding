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

        boolean verify = JWTUtil.verifyJwtToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJ3ZWIiLCJuYmYiOjE2MDMzODQ2ODgsImlzcyI6Imlzc3VlciIsIlVTRVJfSUQiOiIxMjM0NTY3OCIsImV4cCI6MTYwMzM4ODI4OCwiaWF0IjoxNjAzMzg0Njg4LCJqdGkiOiI2YTQ0MjU1MC0wZmI4LTQ1NjUtYWRmYy02NTdmNTZiMTUwNTcifQ.e5MfDbWy_StvLVVI5EQxvj0Zt-uch9SPWOvufPzRlo74dFNIoRFPHZvSagIcKRqcR0k7ukbGXRml4o-fv-UhPc7KDGSUPFmPm0SDLwzu_pPailzV4qtLHb9h4ev4g2xVXcw9-lb42nxlM3f_fVyyazpH2YnhUcLsdiCNEB-HCp6hvKQ9Z4qq1g877tMsFJ1vZrKeSUliyJ4IEGPWQM_t6FutaFdFNCSJY_VWv70A8pwLrxPF064l5FD0HYhaYzXD45dFU8ALDuaSo22A_2yQ0rNn5FD2o3dv1rwyrnoajlS3PXlnbOTSbDZD6yZCh_peXs5H3iHPJ2ULN75YGMRnTQ");
//        boolean verify = JWTUtil.verifyJwtToken(token);
        Assertions.assertTrue(verify);

        /*Map<String, String> claims = JWTUtil.verifyJwtTokenAndClaims(token);
        claims.forEach((k, v) -> System.out.println(k + ":" + v));*/
    }

    @Test
    public void verifyToken() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJ3ZWIiLCJuYmYiOjE2MDMzODQ2MTYsImlzcyI6Imlzc3VlciIsIlVTRVJfSUQiOiIxMjM0NTY3OCIsImV4cCI6MTYwMzM4ODIxNiwiaWF0IjoxNjAzMzg0NjE2LCJqdGkiOiI2YTk0OTQ0MS1iOGQ2LTQ2MjUtOTU4My1iZmE4OTUxYTdjNGEifQ.5JugbXna1ZpYdvQOZ8F3Snnsr0KeO7-PH9OT7FJuN38";
        boolean jwtToken = JWTUtil.verifyJwtToken(token);
        Assertions.assertTrue(jwtToken);
    }

}
