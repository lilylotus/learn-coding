package cn.nihility.unify.util;

import cn.nihility.unify.exception.UnifyException;
import cn.nihility.unify.pojo.UnifyResultCode;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JWTUtil {

    private final static Logger log = LoggerFactory.getLogger(JWTUtil.class);
    private final static String SECRET_KEY = "SECRET_KEY";
    /* 过期时间 30 分钟 */
    private final static int EXPIRE_MILLIS_SECONDS = 30 * 60 * 1000;

    private final static Algorithm HMAC256 = Algorithm.HMAC256(SECRET_KEY);
    private final static Algorithm RSA256 = generateRS256();
    private final static Algorithm CURRENT_USE_ALGORITHM = HMAC256;

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

        /* 载体组装 (Payload) */
        JWTCreator.Builder builder = JWT.create();
        /* 发布者： 校验需要和签发的一致 */
        builder.withIssuer("issuer");
        /* 接收者： 签发的时候为 [a.com, b.com] 验证的时候需要是其中一个*/
        builder.withAudience(audience);
        /* 生效时间:  */
        builder.withNotBefore(new Date());
        /* 生成签名的时间: */
        builder.withIssuedAt(new Date());
        /* token 有效时间: */
        builder.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_MILLIS_SECONDS));
        /*  */
        builder.withJWTId(UUID.randomUUID().toString());
        /* 自定义载荷 */
        if (params != null && params.size() > 0) {
            params.forEach(builder::withClaim);
        }

        return builder.sign(algorithm);
    }

    private static Algorithm generateRS256() {
        RSAUtil.RSAKeyPair keyPair = RSAUtil.getRSA256KeyPair();
        return Algorithm.RSA256(keyPair.getRsaPublicKey(), keyPair.getRsaPrivateKey());
    }

    /*==================== generate RSA256/HMAC256 token and verify token ====================*/
    public static String generateJwtToken(Map<String, String> params) {
        return createJwt(params, CURRENT_USE_ALGORITHM);
    }

    public static boolean verifyJwtToken(String token) {
        return verifyJwtToken(token, null);
    }

    public static boolean verifyJwtToken(String token, Map<String, String> params) throws UnifyException {
        if (StringUtils.isBlank(token)) {
            throw new UnifyException("TOKEN IS NULL", UnifyResultCode.UNAUTHORIZED);
        }
        return verifierToken(token, CURRENT_USE_ALGORITHM, params);
    }

    public static Map<String, String> verifyJwtTokenAndClaims(String token) throws UnifyException {
        Map<String, String> claims = new HashMap<>(8);
        if (!verifyJwtTokenAndClaims(token, RSA256, claims)) {
            throw new UnifyException("Verify Token And Get Claims Error", UnifyResultCode.UNAUTHORIZED);
        }
        return claims;
    }

    /* ==================== verify ==================== */

    /**
     * default algorithm HMAC256
     */
    public static boolean verifierToken(String token) {
        return verifierToken(token, RSA256);
    }

    public static boolean verifierToken(String token, Map<String, String> params) {
        return verifierToken(token, RSA256, params);
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
        Verification verification = JWT.require(algorithm);
        if (null != params && params.size() > 0) {
            params.forEach(verification::withClaim);
        }
        JWTVerifier verifier = verification.build();
        try {
            DecodedJWT verify = verifier.verify(token);
            if (log.isDebugEnabled()) {
                log.debug("verify: [{}], Header: [{}] , payLoad: [{}], signature: [{}], Expire: [{}], Issuer [{}], Audience [{}]",
                        verify, verify.getHeader(), verify.getPayload(), verify.getSignature(), verify.getExpiresAt(),
                        verify.getIssuer(), verify.getAudience());

                verify.getClaims().forEach((k, v) -> log.debug("[{}] : [{}]", k, v.asString()));
            }
        } catch (TokenExpiredException ex) {
            throw new UnifyException("Verify Token Expire", ex, UnifyResultCode.UNAUTHORIZED);
        } catch (JWTVerificationException e) {
            throw new UnifyException("Verify Token Error", e, UnifyResultCode.UNAUTHORIZED);
        }
        return true;
    }

    public static boolean verifyJwtTokenAndClaims(String token, Algorithm algorithm, final Map<String, String> claims) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = verifier.verify(token);
            Map<String, Claim> claimMap = verify.getClaims();

            final HashSet<String> claimKeySet = new HashSet<>(8);
            claimKeySet.add("aud");
            claimKeySet.add("nbf");
            claimKeySet.add("iss");
            claimKeySet.add("exp");
            claimKeySet.add("jti");
            claimKeySet.add("iat");

            claimMap.entrySet().stream().filter(k -> !claimKeySet.contains(k.getKey()))
                    .forEach((k -> claims.put(k.getKey(), k.getValue().asString())));

            if (log.isDebugEnabled()) {
                log.debug("verify: [{}], Header: [{}] , payLoad: [{}], signature: [{}], Expire: [{}], Issuer [{}], Audience [{}]",
                        verify, verify.getHeader(), verify.getPayload(), verify.getSignature(), verify.getExpiresAt(),
                        verify.getIssuer(), verify.getAudience());

                claimMap.forEach((k, v) -> log.debug("[{}] : [{}]", k, v.asString()));
            }
        } catch (TokenExpiredException ex) {
            throw new UnifyException("Verify Token Expire", UnifyResultCode.UNAUTHORIZED);
        } catch (JWTVerificationException e) {
            throw new UnifyException("Verify Token Error", UnifyResultCode.UNAUTHORIZED);
        }
        return true;
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
