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

        String jwtHMAC256 = JWTUtil.createJwtHMAC256(params);
        String jwtRS256 = JWTUtil.createJwtRS256(params);

        System.out.println(jwtHMAC256);
        System.out.println(jwtRS256);

        System.out.println("==================================");
        JWTUtil.verifierTokenHMAC256(jwtHMAC256, params);
        System.out.println("==================================");
        JWTUtil.verifierTokenRS256(jwtRS256, params);
    }

    @Test
    public void testJwtGenerate() {
        try {
            RSA256Key rsa256Key = SecretKeyUtils.getRSA256Key();
            RSAPublicKey publicKey = rsa256Key.getPublicKey();
            RSAPrivateKey privateKey = rsa256Key.getPrivateKey();

            System.out.println(publicKey);
            System.out.println(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGenerateRSAKey() {
        try {

            Map<String, Object> keyMap = SecretKeyUtils.initKey(4096);

            RSAPublicKey publicKey = (RSAPublicKey) keyMap.get(SecretKeyUtils.PUBLIC_KEY);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get(SecretKeyUtils.PRIVATE_KEY);

            String algorithm = publicKey.getAlgorithm();
            String format = publicKey.getFormat();
            BigInteger exponent = publicKey.getPublicExponent();
            BigInteger modulus = publicKey.getModulus();
            byte[] publicKeyEncoded = publicKey.getEncoded();
            System.out.println("publicKey algorithm [" + algorithm + "], format [" + format + "], exponent [" + exponent + "], modulus [" + modulus + "]");
            String publicEncoded = SecretKeyUtils.encryptBASE64(publicKeyEncoded);
            System.out.println(publicEncoded);

            algorithm = privateKey.getAlgorithm();
            format = privateKey.getFormat();
            exponent = privateKey.getPrivateExponent();
            modulus = privateKey.getModulus();
            byte[] privateKeyEncoded = privateKey.getEncoded();
            System.out.println("privateKey algorithm [" + algorithm + "], format [" + format + "], exponent [" + exponent + "], modulus [" + modulus + "]");
            String privateEncoded = SecretKeyUtils.encryptBASE64(privateKeyEncoded);
            System.out.println(privateEncoded);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class TestJwt implements Serializable {
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
