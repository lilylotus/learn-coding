package cn.nihility.common.util;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

class JwkUtilsTest {

    private final String audience = "web";
    private final String subject = "jwk test";
    private final int expireMinutes = 10;

    @Test
    void simpleGenerateJwt() throws JoseException, MalformedClaimException {
        // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
        RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);

        // Give the JWK a Key ID (kid), which is just the polite thing to do
        rsaJsonWebKey.setKeyId("k1");

        // Create the Claims, which will be the content of the JWT
        JwtClaims claims = new JwtClaims();
        // who creates the token and signs it
        claims.setIssuer("Issuer");
        // to whom the token is intended to be sent
        claims.setAudience("Audience");
        // the subject/principal is whom the token is about
        claims.setSubject("subject");
        // time when the token will expire (10 minutes from now)
        claims.setExpirationTimeMinutesInTheFuture(10);
        // a unique identifier for the token
        claims.setGeneratedJwtId();
        // when the token was issued/created (now)
        claims.setIssuedAtToNow();
        // time before which the token is not yet valid (2 minutes ago)
        claims.setNotBeforeMinutesInThePast(2);
        // additional claims/attributes about the subject can be added
        claims.setClaim("email", "mail@example.com");
        // multi-valued claims work too and will end up as a JSON array
        List<String> groups = Arrays.asList("group-one", "other-group", "group-three");
        claims.setStringListClaim("groups", groups);


        // A JWT is a JWS and/or a JWE with JSON claims as the payload.
        // In this example it is a JWS so we create a JsonWebSignature object.
        JsonWebSignature jws = new JsonWebSignature();

        // The payload of the JWS is JSON content of the JWT Claims
        jws.setPayload(claims.toJson());

        // The JWT is signed using the private key
        jws.setKey(rsaJsonWebKey.getPrivateKey());

        // Set the Key ID (kid) header because it's just the polite thing to do.
        // We only have one key in this example but a using a Key ID helps
        // facilitate a smooth key rollover process
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        // Sign the JWS and produce the compact serialization or the complete JWT/JWS
        // representation, which is a string consisting of three dot ('.') separated
        // base64url-encoded parts in the form Header.Payload.Signature
        // If you wanted to encrypt it, you can simply set this jwt as the payload
        // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
        String jwt = jws.getCompactSerialization();
        System.out.println("JWT: " + jwt);


        List<JsonWebKey> jsonWebKeys = new ArrayList<>();
        JwksVerificationKeyResolver jwksVerificationKeyResolver = new JwksVerificationKeyResolver(jsonWebKeys);

        // Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
        // be used to validate and process the JWT.
        // The specific validation requirements for a JWT are context dependent, however,
        // it typically advisable to require a (reasonable) expiration time, a trusted issuer, and
        // and audience that identifies your system as the intended recipient.
        // If the JWT is encrypted too, you need only provide a decryption key or
        // decryption key resolver to the builder.
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
            // the JWT must have an expiration time
            .setRequireExpirationTime()
            // allow some leeway in validating time based claims to account for clock skew
            .setAllowedClockSkewInSeconds(30)
            // the JWT must have a subject claim
            .setRequireSubject()
            // whom the JWT needs to have been issued by
            .setExpectedIssuer("Issuer")
            //.setVerificationKeyResolver(jwksVerificationKeyResolver)
            // to whom the JWT is intended for
            .setExpectedAudience("Audience")
            // verify the signature with the public key
            .setVerificationKey(rsaJsonWebKey.getKey())
            // only allow the expected signature algorithm(s) in the given context
            // which is only RS256 here
            .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
            // create the JwtConsumer instance
            .build();

        try {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
            System.out.println("JWT validation succeeded! " + jwtClaims);
        } catch (InvalidJwtException e) {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
            // Hopefully with meaningful explanations(s) about what went wrong.
            System.out.println("Invalid JWT! " + e);

            // Programmatic access to (some) specific reasons for JWT invalidity is also possible
            // should you want different error handling behavior for certain conditions.

            // Whether or not the JWT has expired being one common reason for invalidity
            if (e.hasExpired()) {
                System.out.println("JWT expired at " + e.getJwtContext().getJwtClaims().getExpirationTime());
            }

            // Or maybe the audience was invalid
            if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID)) {
                System.out.println("JWT had wrong audience: " + e.getJwtContext().getJwtClaims().getAudience());
            }
        }
    }

    @Test
    void generateJws() throws JoseException {

        RsaUtils.RsaKeyPairHolder rsaKeyPairHolder = RsaUtils.generateRsaKeyPair("seed");
        RSAPrivateKey rsaPrivateKey = rsaKeyPairHolder.getRsaPrivateKey();
        RSAPublicKey rsaPublicKey = rsaKeyPairHolder.getRsaPublicKey();
        System.out.println(rsaKeyPairHolder);

        RsaJsonWebKey key = new RsaJsonWebKey(rsaPublicKey);
        key.setPrivateKey(rsaPrivateKey);
        key.setKeyId("k1");
        key.setAlgorithm(AlgorithmIdentifiers.RSA_USING_SHA256);

        System.out.println(key.toJson());

        Map<String, Object> claims = new HashMap<>(8);
        claims.put("id", UuidUtils.jdkUuid());
        claims.put("name", "jwk-test");
        String jwt = JwkUtils.generateJwt(audience, subject, expireMinutes, key, claims);

        Assertions.assertNotNull(jwt);
        System.out.println(jwt);

        JwtClaims jwtClaims = JwkUtils.jwtConsumer(jwt, key);
        System.out.println(jwtClaims.getRawJson());

        JwtClaims jwtClaims1 = JwkUtils.jwtConsumerByResolver(jwt, Collections.singletonList(key));
        System.out.println(jwtClaims1.getRawJson());
    }

}
