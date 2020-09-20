package cn.nihility.boot.jwt;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dandelion
 * @date 2020:07:02 21:02
 */
public class JwtTest {

    @Test
    public void testGenerateJwtToken() {
        TestJwt jwt = new TestJwt("test jwt", 1);
        String jwtString = JSON.toJSONString(jwt);
        System.out.println(jwtString);

        Map<String, String> params = new HashMap<>();
        params.put("user", jwtString);
        params.put("password", "Jwt Password");

        String jwtHMAC256 = JWTUtil.createJwt(params);
        String jwtRS256 = JWTUtil.createJwtRSA256(params);

        System.out.println(jwtHMAC256);
        System.out.println(jwtRS256);

        System.out.println("==================================");
        JWTUtil.verifierToken(jwtHMAC256, params);
        System.out.println("==================================");
        JWTUtil.verifierTokenRSA256(jwtRS256, params);
    }

    @Test
    public void testJwtGenerate() {
        try {
            SecretKeyUtils.RSAKeyPair rsa256Key = SecretKeyUtils.getRSA256KeyPair();
            RSAPublicKey publicKey = rsa256Key.getRsaPublicKey();
            RSAPrivateKey privateKey = rsa256Key.getRsaPrivateKey();

            System.out.println(publicKey);
            System.out.println(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGenerateRSAKey() {
        try {

            SecretKeyUtils.RSAKeyPair keyPair = SecretKeyUtils.initRSAKeyPair(4096);

            RSAPublicKey publicKey = keyPair.getRsaPublicKey();
            RSAPrivateKey privateKey = keyPair.getRsaPrivateKey();

            String algorithm = publicKey.getAlgorithm();
            String format = publicKey.getFormat();
            BigInteger exponent = publicKey.getPublicExponent();
            BigInteger modulus = publicKey.getModulus();
            byte[] publicKeyEncoded = publicKey.getEncoded();
            System.out.println("publicKey algorithm [" + algorithm + "], format [" + format + "], exponent [" + exponent + "], modulus [" + modulus + "]");
            String publicEncode = Base64Coded.encode(publicKeyEncoded);
            System.out.println(publicEncode);

            algorithm = privateKey.getAlgorithm();
            format = privateKey.getFormat();
            exponent = privateKey.getPrivateExponent();
            modulus = privateKey.getModulus();
            byte[] privateKeyEncoded = privateKey.getEncoded();
            System.out.println("privateKey algorithm [" + algorithm + "], format [" + format + "], exponent [" + exponent + "], modulus [" + modulus + "]");
            String privateEncoded = Base64Coded.encode(privateKeyEncoded);
            System.out.println(privateEncoded);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class TestJwt implements Serializable {
        private static final long serialVersionUID = 8606546306516594159L;

        String name;
        Integer id;

        public TestJwt(String name, Integer id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", id=" + id +
                    '}';
        }
    }

}
