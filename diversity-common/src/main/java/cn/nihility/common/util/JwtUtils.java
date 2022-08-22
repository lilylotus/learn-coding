package cn.nihility.common.util;

import cn.nihility.common.exception.JwtParseException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * jwt token 工具类
 */
public final class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private static final long DEFAULT_JWT_EXPIRE_DURATION = 7200L;
    public static final Algorithm HMAC256;
    public static final Algorithm HMAC512;
    public static final Algorithm RSA256;
    public static final Algorithm RSA512;

    static {
        Algorithm rsa256 = null;
        Algorithm rsa512 = null;
        String randomSecretKey = UUID.randomUUID().toString().replace("-", "");
        logger.info("Jwt Secret Key [{}]", randomSecretKey);

        HMAC256 = Algorithm.HMAC256(randomSecretKey);
        HMAC512 = Algorithm.HMAC512(randomSecretKey);

        try {
            RsaUtils.RsaKeyPairHolder keyPairHolder = RsaUtils.generateRsaKeyPair(randomSecretKey);
            logger.info("jwt rsa private key [{}], public key [{}]",
                keyPairHolder.getPrivateKey(), keyPairHolder.getPublicKey());
            rsa256 = Algorithm.RSA256(keyPairHolder.getRsaPublicKey(), keyPairHolder.getRsaPrivateKey());
            rsa512 = Algorithm.RSA512(keyPairHolder.getRsaPublicKey(), keyPairHolder.getRsaPrivateKey());
        } catch (NoSuchAlgorithmException e) {
            logger.error("generate ras key pair error", e);
        }

        RSA256 = rsa256;
        RSA512 = rsa512;
    }

    private JwtUtils() {
    }

    public static JwtHolder createJwtToken(final String id, long duration, Map<String, String> claims, final Algorithm algorithm) {
        JWTCreator.Builder builder = JWT.create();

        // 设置jti(JWT ID)：是 JWT 的唯一标识
        builder.withJWTId(id);
        // 发布者
        builder.withIssuer("api");
        // 接收者
        //builder.withAudience("app", "web");
        // 生效时间
        //builder.withNotBefore(new Date());
        // 生成签名的时间
        builder.withIssuedAt(new Date());
        final long expireSeconds = (duration <= 0L ? DEFAULT_JWT_EXPIRE_DURATION : duration);
        // token 有效时间
        builder.withExpiresAt(new Date(System.currentTimeMillis() + (expireSeconds * 1000L)));

        // payload
        if (null != claims && !claims.isEmpty()) {
            claims.forEach(builder::withClaim);
        }

        String jwtToken = builder.sign(algorithm);

        return new JwtHolder(jwtToken, expireSeconds);
    }

    public static JwtHolder createJwtToken(final String id, long duration, Map<String, String> claims) {
        return createJwtToken(id, duration, claims, HMAC256);
    }

    public static JwtHolder createJwtToken(String id, Map<String, String> claims) {
        return createJwtToken(id, 0L, claims);
    }

    public static DecodedJWT verifyJwtToken(String jwt, final Algorithm algorithm) throws JwtParseException {
        if (StringUtils.isBlank(jwt)) {
            throw new JwtParseException("解析 Jwt 参数不可为空");
        }
        try {
            return JWT.require(algorithm).build().verify(jwt);
        } catch (TokenExpiredException e) {
            logger.error("Jwt [{}] 已过期失效", jwt);
            throw new JwtParseException("Jwt 已过期失效", e);
        } catch (AlgorithmMismatchException e) {
            logger.error("该 Jwt [{}] 算法不匹配", jwt);
            throw new JwtParseException("Jwt 算法不匹配", e);
        } catch (SignatureVerificationException e) {
            logger.error("该 Jwt [{}] 涉嫌伪造，签名校验错误", jwt);
            throw new JwtParseException("Jwt 签名校验错误", e);
        } catch (InvalidClaimException e) {
            logger.error("该 Jwt [{}] 的 Claim 校验失败", jwt);
            throw new JwtParseException("Jwt Claim 校验失败", e);
        } catch (JWTDecodeException e) {
            logger.error("解析 Jwt [{}] 异常", jwt);
            throw new JwtParseException("解析 Jwt 失败", e);
        }
    }

    public static DecodedJWT verifyJwtToken(String jwt) throws JwtParseException {
        return verifyJwtToken(jwt, HMAC256);
    }

    public static String obtainJwtClaim(String claim, String jwt) throws JwtParseException {
        return verifyJwtToken(jwt).getClaim(claim).asString();
    }

    public static Map<String, String> obtainJwtClaims(String jwt) throws JwtParseException {
        final Map<String, String> result = new HashMap<>();
        Map<String, Claim> claims = verifyJwtToken(jwt).getClaims();
        claims.forEach((k, v) -> result.put(k, v.asString()));
        return result;
    }

    public static String jwtDecodeClaim(String claim, String jwt) throws JwtParseException {
        try {
            return JWT.decode(jwt).getClaim(claim).asString();
        } catch (JWTDecodeException ex) {
            logger.error("Jwt [{}] 格式有误，解码异常", jwt);
            throw new JwtParseException("Jwt 格式有误，解码失败", ex);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class JwtHolder implements Serializable {

        private static final long serialVersionUID = -7847570899699347130L;

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private long expiresIn;

        @JsonProperty("token_type")
        private String tokenType = "Bearer";

        public JwtHolder(String accessToken, long expiresIn) {
            this.accessToken = accessToken;
            this.expiresIn = expiresIn;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public String toString() {
            return "JwtHolder{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", tokenType='" + tokenType + '\'' +
                '}';
        }

    }

}
