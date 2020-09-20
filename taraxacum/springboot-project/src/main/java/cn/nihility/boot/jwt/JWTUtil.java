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
    private final static Algorithm RSA256 = generateRS256();

    /* ==================== generate jwt token ==================== */

    /**
     * default algorithm HMAC256
     */
    public static String createJwt(Map<String, String> params) {
        return createJwt(params, HMAC256);
    }

    public static String createJwtRSA256(Map<String, String> params) {
        return createJwt(params, RSA256);
    }

    public static String createJwt(Map<String, String> params, Algorithm algorithm) {
        String[] audience  = {"app","web"};

        JWTCreator.Builder builder = JWT.create();
        builder.withIssuer("auth");  // 发布者
        builder.withAudience(audience); // 接收者
        builder.withNotBefore(new Date()); // 生效时间
        builder.withIssuedAt(new Date()); // 生成签名的时间
        builder.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_MILLIS_SECONDS)); // token 有效时间
        builder.withJWTId(UUID.randomUUID().toString());
        if (params != null && params.size() > 0) {
            params.forEach(builder::withClaim);
        }

        return builder.sign(algorithm);
    }

    private static Algorithm generateRS256() {
        SecretKeyUtils.RSAKeyPair keyPair = SecretKeyUtils.getRSA256KeyPair();
        return Algorithm.RSA256(keyPair.getRsaPublicKey(), keyPair.getRsaPrivateKey());
    }

    /* ==================== verify ==================== */

    /**
     * default algorithm HMAC256
     */
    public static boolean verifierToken(String token) {
        return verifierToken(token, HMAC256);
    }

    public static boolean verifierToken(String token, Map<String, String> params) {
        return verifierToken(token, HMAC256, params);
    }

    public static boolean verifierTokenRSA256(String token) {
        return verifierToken(token, RSA256);
    }

    public static boolean verifierTokenRSA256(String token, Map<String, String> params) {
        return verifierToken(token, RSA256, params);
    }

    public static boolean verifierToken(String token, Algorithm algorithm) {
        return verifierToken(token, algorithm, null);
    }

    public static boolean verifierToken(String token, Algorithm algorithm, Map<String, String> params) {
        Verification verification = JWT.require(algorithm).withIssuer("auth");
        if (null != params && params.size() > 0) {
            params.forEach(verification::withClaim);
        }
        JWTVerifier verifier = verification.build();
        boolean ok = true;
        try {
            DecodedJWT verify = verifier.verify(token);

            System.out.println("verify: " + verify);
            System.out.println("Header: " + verify.getHeader());
            System.out.println("payLoad: " + verify.getPayload());
            System.out.println("signature: " + verify.getSignature());
            System.out.println("Expire : " + verify.getExpiresAt());

            verify.getClaims().forEach((k, v) -> System.out.println(k + ":" + v.asString()));
        } catch (JWTVerificationException e) {
            System.out.println("inValid token error : " + e.getMessage());
            ok = false;
        }
        return ok;
    }

    public static void main(String[] args) {
        Map<String, String> param = new HashMap<>();
        param.put("name", "小明");
        param.put("age", "20");

        String jwt = createJwt(param, HMAC256);
        System.out.println("jwt " + jwt);
        verifierToken(jwt, HMAC256, param);

        System.out.println("============================");
        jwt = createJwt(param, RSA256);
        //String jwtRS256 = createJwtRS256(param);
        //System.out.println(jwtRS256);
        System.out.println("rsa256 jwt : " + jwt);

        param.put("age", "22");
        verifierToken(jwt, RSA256, param);
    }

}
