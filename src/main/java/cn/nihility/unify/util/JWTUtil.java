package cn.nihility.unify.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JWTUtil {

    private final static Logger log = LoggerFactory.getLogger(JWTUtil.class);
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
        String[] audience = {"web"};

        Date date = new Date();
        JWTCreator.Builder builder = JWT.create();
        builder.withIssuer("auth");  // 发布者
        builder.withAudience(audience); // 接收者
        builder.withNotBefore(date); // 生效时间
        builder.withIssuedAt(date); // 生成签名的时间
        builder.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_MILLIS_SECONDS)); // token 有效时间
        builder.withJWTId(UUID.randomUUID().toString());
        if (params != null && params.size() > 0) {
            params.forEach(builder::withClaim);
        }

        return builder.sign(algorithm);
    }

    private static Algorithm generateRS256() {
        RSAUtil.RSAKeyPair keyPair = RSAUtil.getRSA256KeyPair();
        return Algorithm.RSA256(keyPair.getRsaPublicKey(), keyPair.getRsaPrivateKey());
    }

    /*==================== generate token and verify token ====================*/
    public static String generateJwtToken(Map<String, String> params) {
        return createJwt(params, RSA256);
    }

    public static boolean verifyJwtToken(String token, Map<String, String> params) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        return verifierToken(token, RSA256, params);
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

            if (log.isDebugEnabled()) {
                log.debug("verify: [{}], Header: [{}] , payLoad: [{}], signature: [{}], Expire: [{}]",
                        verify, verify.getHeader(), verify.getPayload(), verify.getSignature(), verify.getExpiresAt());
                verify.getClaims().forEach((k, v) -> log.debug("[{}] : [{}]", k, v.asString()));
            }

        } catch (JWTVerificationException e) {
            log.error("inValid token", e);
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
