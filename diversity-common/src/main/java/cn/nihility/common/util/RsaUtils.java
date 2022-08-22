package cn.nihility.common.util;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaUtils {

    /**
     * 密钥长度，长度越长速度越慢
     */
    public static final int KEY_SIZE = 2048;

    public static final String STRING_RSA = "RSA";
    /**
     * RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING
     * RSA/None/OAEPWITHSHA-256ANDMGF1PADDING
     */
    private static final String STRING_CIPHER_INSTANCE = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";

    public static RsaKeyPairHolder generateRsaKeyPair(String seed) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(STRING_RSA);
        if (null == seed) {
            keyPairGenerator.initialize(KEY_SIZE);
        } else {
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom(seed.getBytes(StandardCharsets.UTF_8)));
        }
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        return new RsaKeyPairHolder(publicKeyString, publicKey, privateKeyString, privateKey);
    }


    /**
     * RSA 公钥加密
     *
     * @param str       加密字符串
     * @param publicKey base64 编码公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(decoded);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(STRING_RSA).generatePublic(x509);
        Cipher cipher = Cipher.getInstance(STRING_CIPHER_INSTANCE);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        byte[] inputByte = Base64.getDecoder().decode(str);
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs = new PKCS8EncodedKeySpec(decoded);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(STRING_RSA).generatePrivate(pkcs);
        Cipher cipher = Cipher.getInstance(STRING_CIPHER_INSTANCE);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

    public static void main(String[] args) throws Exception {
        RsaKeyPairHolder keyPair = generateRsaKeyPair("random");
        System.out.println(keyPair);
        String str = "加密字符串";

        String secret = AesUtils.generateSecret(128);
        System.out.println("secret [" + secret + "]");
        String encrypt = AesUtils.encrypt(str, secret);
        System.out.println("encrypt content [" + encrypt + "]");

        String secretEncrypt = encrypt(secret, keyPair.getPublicKey());
        System.out.println("encrypt secret [" + secretEncrypt + "]");
        String decryptSecret = decrypt(secretEncrypt, keyPair.getPrivateKey());
        System.out.println("decrypt secret [" + decryptSecret + "]");

        String decrypt = AesUtils.decrypt(encrypt, decryptSecret);
        System.out.println("decrypt content [" + decrypt + "]");

    }

    public static class RsaKeyPairHolder {

        private String publicKey;

        private RSAPublicKey rsaPublicKey;

        private String privateKey;

        private  RSAPrivateKey rsaPrivateKey;

        public RsaKeyPairHolder(String publicKey, RSAPublicKey rsaPublicKey,
                                String privateKey, RSAPrivateKey rsaPrivateKey) {
            this.publicKey = publicKey;
            this.rsaPublicKey = rsaPublicKey;
            this.privateKey = privateKey;
            this.rsaPrivateKey = rsaPrivateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public RSAPublicKey getRsaPublicKey() {
            return rsaPublicKey;
        }

        public RSAPrivateKey getRsaPrivateKey() {
            return rsaPrivateKey;
        }

        @Override
        public String toString() {
            return "publicKey=" + publicKey + "\n" +
                    "privateKey=" + privateKey;
        }
    }

}
