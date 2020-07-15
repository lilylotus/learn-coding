package cn.nihility.boot.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JWTUtil {

    private final static String SECRET_KEY = "SECRET_KEY";
    private final static int EXPIRE_MILLIS_SECONDS = 60 * 60 * 1000;
    private final static Algorithm HMAC256 = Algorithm.HMAC256(SECRET_KEY);

    public static String createJwtHMAC256(Map<String, String> params) {
        return createJwt(params, HMAC256);
    }

    public static String createJwt(Map<String, String> params, Algorithm algorithm) {

        String[] audience  = {"app","web"};
        JWTCreator.Builder builder = JWT.create();
        builder.withIssuer("auth0");  // 发布者
        builder.withAudience(audience); // 接收者
        builder.withNotBefore(new Date()); // 生效时间
        builder.withIssuedAt(new Date()); // 生成签名的时间
        builder.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_MILLIS_SECONDS)); // token 有效时间
        builder.withJWTId(UUID.randomUUID().toString());
        params.forEach(builder::withClaim);

        return builder.sign(algorithm);
    }

    public static String createJwtRS256(Map<String, String> params) {
        return createJwt(params, generateRS256());
    }

    public static void verifierTokenHMAC256(String token) {
        verifierToken(token, null, HMAC256);
    }

    public static void verifierTokenHMAC256(String token, Map<String, String> params) {
        verifierToken(token, params, HMAC256);
    }

    public static Algorithm generateRS256() {
        try {
            RSA256Key rsa256Key = SecretKeyUtils.getRSA256Key();
            return Algorithm.RSA256(rsa256Key.getPublicKey(), rsa256Key.getPrivateKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void verifierTokenRS256(String token) {
        verifierToken(token, null, generateRS256());
    }

    public static void verifierTokenRS256(String token, Map<String, String> params) {
        verifierToken(token, params, generateRS256());
    }

    public static void verifierToken(String token, Map<String, String> params, Algorithm algorithm) {
        Verification verification = JWT.require(algorithm)
                .withIssuer("auth0")
                .withAudience("app", "web");

        if (null != params) {
            params.forEach(verification::withClaim);
        }

        JWTVerifier verifier = verification.build();

        try {
            DecodedJWT verify = verifier.verify(token);
            System.out.println(verify);

            String header = verify.getHeader();
            String payload = verify.getPayload();
            String signature = verify.getSignature();

            System.out.println("Header: " + header);
            System.out.println("payLoad: " + payload);
            System.out.println("signature: " + signature);

            System.out.println("Expire : " + verify.getExpiresAt());

            verify.getClaims().forEach((k, v) -> System.out.println(k + ":" + v.asString()));
        } catch (JWTVerificationException e) {
            System.out.println("inValid token");
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) {
        Map<String, String> param = new HashMap<>();
        param.put("one", "one");
        param.put("two", "TWO");

        String jwt = createJwtHMAC256(param);
        System.out.println(jwt);

        String tokenOk = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJvbmUiOiJvbmUiLCJpc3MiOiJhdXRoMCIsImV4cCI6MTU5MjU0MjUxNywiaWF0IjoxNTkyNTM4OTE3LCJ0d28iOiJUV08ifQ.7Pp8DWHMtYmClGzTqn-w3LMt3vY9cAC5bIX6k-wZXAI";
        String tokenExpire = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJvbmUiOiJvbmUiLCJpc3MiOiJhdXRoMCIsImV4cCI6MTU5MjUzNTI4MiwiaWF0IjoxNTkyNTM4ODgyLCJ0d28iOiJUV08ifQ.s-LjCFhPYNij52cG5Zaa5jUTPOFse97nCwM9uPRj4eI";
        verifierTokenHMAC256(jwt, param);


        System.out.println("============================");
        String jwtRS256 = createJwtRS256(param);
        System.out.println(jwtRS256);

        verifierTokenRS256(jwtRS256, param);
    }

}
