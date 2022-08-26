package cn.nihility.common.util;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.*;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver;
import org.jose4j.keys.resolvers.VerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JSON Web Key (JWK) - https://datatracker.ietf.org/doc/html/draft-ietf-jose-json-web-key-41
 * <p>
 * https://openid.net/developers/jwt/
 * <p>
 * https://bitbucket.org/b_c/jose4j/wiki/Home <br/>
 * <p>
 * Digital Signature or MAC Algorithm  |  JWS "alg" Parameter Value <br/>
 * HMAC using SHA-2  | HS256, HS384 and HS512 <br/>
 * RSASSA-PKCS1-V1_5 Digital Signatures with with SHA-2 | RS256, RS384 and RS512 <br/>
 * Elliptic Curve Digital Signatures (ECDSA) with SHA-2 | ES256, ES384 and ES512 <br/>
 * RSASSA-PSS Digital Signatures with SHA-2 | PS256†, PS384† and PS512† <br/>
 * Unsigned Plaintext | none <br/>
 */
public class JwkUtils {

    private static final Logger log = LoggerFactory.getLogger(JwkUtils.class);

    private static final int DEFAULT_JWK_EXPIRE_MINUTES = 60;

    private JwkUtils() {
    }

    public static List<String> getJwsSignatureAlgorithmsHMAC() {
        return Collections.unmodifiableList(Arrays.asList(AlgorithmIdentifiers.HMAC_SHA256,
            AlgorithmIdentifiers.HMAC_SHA384, AlgorithmIdentifiers.HMAC_SHA512));
    }

    public static List<String> getJwsSignatureAlgorithmsRSA() {
        return Collections.unmodifiableList(Arrays.asList(AlgorithmIdentifiers.RSA_USING_SHA256,
            AlgorithmIdentifiers.RSA_USING_SHA384, AlgorithmIdentifiers.RSA_USING_SHA512));
    }

    public static List<String> getJwsSignatureAlgorithmsECDSA() {
        return Collections.unmodifiableList(Arrays.asList(AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256,
            AlgorithmIdentifiers.ECDSA_USING_P384_CURVE_AND_SHA384, AlgorithmIdentifiers.ECDSA_USING_P521_CURVE_AND_SHA512));
    }

    public static List<String> getJwsSignatureAlgorithmsRSAPSS() {
        return Collections.unmodifiableList(Arrays.asList(AlgorithmIdentifiers.RSA_PSS_USING_SHA256,
            AlgorithmIdentifiers.RSA_PSS_USING_SHA384, AlgorithmIdentifiers.RSA_PSS_USING_SHA512));
    }

    /**
     * 生成 rsa json jwk 签名公、私钥
     *
     * @param bits 1024/2048/4096
     * @return RsaJsonWebKey
     * @throws JoseException 异常
     */
    public static RsaJsonWebKey generateRsaJsonWebKey(int bits) throws JoseException {
        RsaJsonWebKey key = RsaJwkGenerator.generateJwk(bits);
        key.setKeyId(UuidUtils.jdkUuid());
        return key;
    }

    /**
     * 生成 octet - HMAC 签名算法
     *
     * @param keyLengthInBits key 长度
     * @return OctetSequenceJsonWebKey
     */
    public static OctetSequenceJsonWebKey generateOctetSequenceJsonWebKey(int keyLengthInBits) {
        return OctJwkGenerator.generateJwk(keyLengthInBits);
    }

    public static JwtClaims generateJwtClaims(String issuer, String audience, String subject, int expireMinutes,
                                              Map<String, Object> claimsAttributes) {
        // Create the Claims, which will be the content of the JWT
        JwtClaims claims = new JwtClaims();
        // who creates the token and signs it
        if (null != issuer) {
            claims.setIssuer(issuer);
        }
        // to whom the token is intended to be sent
        if (null != audience) {
            claims.setAudience(audience);
        }
        // the subject/principal is whom the token is about
        if (null != subject) {
            claims.setSubject(subject);
        }
        // time when the token will expire (10 minutes from now)
        expireMinutes = (expireMinutes > 0 ? expireMinutes : DEFAULT_JWK_EXPIRE_MINUTES);
        claims.setExpirationTimeMinutesInTheFuture(expireMinutes);
        // a unique identifier for the token
        claims.setGeneratedJwtId();
        // when the token was issued/created (now)
        claims.setIssuedAtToNow();
        // time before which the token is not yet valid (2 minutes ago)
        claims.setNotBeforeMinutesInThePast(2);
        // additional claims/attributes about the subject can be added
        if (null != claimsAttributes) {
            claimsAttributes.forEach(claims::setClaim);
        }
        return claims;
    }

    public static String generateJwt(String audience, String subject, int expireMinutes, JsonWebKey signatureKey,
                                     Map<String, Object> claimsAttributes) throws JoseException {
        if (null == signatureKey) {
            return null;
        }

        Key privateKey;
        String keyId;
        String alg;

        if (signatureKey instanceof PublicJsonWebKey) {
            PublicJsonWebKey pk = (PublicJsonWebKey) signatureKey;
            privateKey = pk.getPrivateKey();
            keyId = pk.getKeyId();
            alg = pk.getAlgorithm();
        } else if (signatureKey instanceof OctetSequenceJsonWebKey) {
            OctetSequenceJsonWebKey ok = (OctetSequenceJsonWebKey) signatureKey;
            privateKey = ok.getKey();
            keyId = ok.getKeyId();
            alg = ok.getAlgorithm();
        } else {
            log.error("不支持的签名算法类型 [{}]", signatureKey);
            throw new IllegalArgumentException("不支持的签名算法类型 [" + signatureKey + "]");
        }

        if (keyId == null) {
            keyId = UuidUtils.jdkUuid();
        }

        JwtClaims jwtClaims = generateJwtClaims("Issuer", audience, subject, expireMinutes, claimsAttributes);
        JsonWebSignature jws = new JsonWebSignature();

        jws.setPayload(jwtClaims.toJson());
        jws.setKey(privateKey);
        jws.setKeyIdHeaderValue(keyId);
        jws.setAlgorithmHeaderValue(alg);

        return jws.getCompactSerialization();
    }

    public static JwtClaims jwtConsumer(String jwt, JsonWebKey signatureKey) {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
            .setRequireExpirationTime()
            .setAllowedClockSkewInSeconds(30)
            .setRequireIssuedAt()
            .setRequireSubject()
            .setSkipDefaultAudienceValidation()
            .setVerificationKey(signatureKey.getKey())
            .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, signatureKey.getAlgorithm())
            .build();
        try {
            return jwtConsumer.processToClaims(jwt);
        } catch (InvalidJwtException e) {
            log.warn("无效 jwt [{}]", jwt, e);
            if (e.hasExpired()) {
                throw new IllegalStateException("jwt 已失效");
            } else {
                throw new IllegalStateException("无效 jwt");
            }
        }
    }

    public static JwtClaims jwtConsumerByResolver(String jwt, List<JsonWebKey> jsonWebKeys) {
        VerificationKeyResolver keyResolver = new JwksVerificationKeyResolver(jsonWebKeys);
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
            .setRequireExpirationTime()
            .setAllowedClockSkewInSeconds(30)
            .setRequireIssuedAt()
            .setRequireSubject()
            .setSkipDefaultAudienceValidation()
            .setVerificationKeyResolver(keyResolver)
            .build();
        try {
            return jwtConsumer.processToClaims(jwt);
        } catch (InvalidJwtException e) {
            log.warn("无效 jwt [{}]", jwt, e);
            if (e.hasExpired()) {
                throw new IllegalStateException("jwt 已失效");
            } else {
                throw new IllegalStateException("无效 jwt");
            }
        }
    }

}
