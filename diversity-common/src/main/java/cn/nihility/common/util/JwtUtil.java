package cn.nihility.common.util;

import cn.nihility.common.exception.JwtParseException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * jwt token 工具类
 */
public final class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private static final long DEFAULT_JWT_EXPIRE_DURATION = 120 * 60 * 1000L;
    private static final Algorithm HMAC256;

    static {
        String randomSecretKey = UUID.randomUUID().toString().replace("-", "");
        if (logger.isDebugEnabled()) {
            logger.debug("Jwt Secret Key [{}]", randomSecretKey);
        }
        HMAC256 = Algorithm.HMAC256(randomSecretKey);
    }

    private JwtUtil() {
    }

    public static String createJwtToken(final String id, long duration, Map<String, String> claims, final Algorithm algorithm) {
        JWTCreator.Builder builder = JWT.create();

        builder.withJWTId(id); // 设置jti(JWT ID)：是 JWT 的唯一标识
        builder.withIssuer("api");  // 发布者
        //builder.withAudience("app", "web"); // 接收者
        //builder.withNotBefore(new Date()); // 生效时间
        builder.withIssuedAt(new Date()); // 生成签名的时间
        final long expireSeconds = (duration <= 0L ? DEFAULT_JWT_EXPIRE_DURATION : Math.min(duration, DEFAULT_JWT_EXPIRE_DURATION));
        builder.withExpiresAt(new Date(System.currentTimeMillis() + expireSeconds)); // token 有效时间

        // payload
        if (null != claims && !claims.isEmpty()) {
            claims.forEach(builder::withClaim);
        }

        return builder.sign(algorithm);
    }

    public static String createJwtToken(final String id, long duration, Map<String, String> claims) {
        return createJwtToken(id, duration, claims, HMAC256);
    }

    public static String createJwtToken(String id, Map<String, String> claims) {
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

}
