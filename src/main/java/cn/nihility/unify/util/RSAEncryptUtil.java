package cn.nihility.unify.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA 需要产生公钥和私钥，当采用公钥加密时，使用私钥解密；采用私钥加密时，使用公钥解密。
 */
public class RSAEncryptUtil {

    private static final Logger log = LoggerFactory.getLogger(RSAEncryptUtil.class);
    private static RSAKeyPair keyPair;

    static {
        try {
            keyPair = genRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            log.error("Init RSA Key Pair Failure", e);
        }
    }

    public static void main(String[] args) throws Exception {
        String msg = "nihao 你好";

        final String encrypt = defaultEncrypt(msg);
        System.out.println("encrypt [" + encrypt + "]");

        final String decrypt = defaultDecrypt(encrypt);
        System.out.println("decrypt [" + decrypt + "]");

        System.out.println("public key encode [" + keyPair.getPublicKeyEncode() + "]");
        System.out.println("private key encode [" + keyPair.getPrivateKeyEncode() + "]");

        System.out.println("--------------");

        KeyPair keyPair = genKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println("public key encode [" + new String(Base64.encodeBase64(publicKey.getEncoded())) + "]");
        System.out.println("private key encode [" + new String(Base64.encodeBase64(privateKey.getEncoded())) + "]");

//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwKT4Um2AHyaY6+6qyFSVUsxSeFgUqmKoTJBTmw311ldyQHoz5YdzhCUaHNu0bfj8O6qVRqDO3FWRxcJbtJm4cO3ynEMkxchI7jwnl5Oy97/1et1ZyjqBxUxyJd5CNeRwX1I5ME4YKfpyueXFt3fKRYgneS3NQRzNMQvzpmvLB3SMWVHak96JBtzcNLBJSzKqUnfnp5gRkIBAuznD9RyRNAI3XpK5qAtyDXJW6SQtVDdE+Tz8ejKQ5kdDdVTZEKsLWDS7EtRGhCxAQKJIeQjzWu75ncHxllXNnX59G3aGt+3p5fnGCZ5iKmA0EFvnnmZGYLYNJoo41thiWO8JRxraOwIDAQAB";
//        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDApPhSbYAfJpjr7qrIVJVSzFJ4WBSqYqhMkFObDfXWV3JAejPlh3OEJRoc27Rt+Pw7qpVGoM7cVZHFwlu0mbhw7fKcQyTFyEjuPCeXk7L3v/V63VnKOoHFTHIl3kI15HBfUjkwThgp+nK55cW3d8pFiCd5Lc1BHM0xC/Oma8sHdIxZUdqT3okG3Nw0sElLMqpSd+enmBGQgEC7OcP1HJE0AjdekrmoC3INclbpJC1UN0T5PPx6MpDmR0N1VNkQqwtYNLsS1EaELEBAokh5CPNa7vmdwfGWVc2dfn0bdoa37enl+cYJnmIqYDQQW+eeZkZgtg0mijjW2GJY7wlHGto7AgMBAAECggEASuRXfYQAWRviO2aPkk3/ty6jqRFlB1BPaWR6fD8TIX7hdduMLWFZMThTTIDFRjOTq4aHfeuqP9IsV5tVl4JjR9PZi5I2y4UvhR5xreXHn7VKs7c13v6b0J4Fil87kkP6w7Ptc7AIiNlKnKaNFORHCH0EC4IY14Vg5/5jfIFPbtfdxKS5fsxx8p5nax9CZ+hk3PBKRn3NMFbXOxU/T/pFWLrzKeY01CoNwjDJ9GjDcopsnfklEtR9x79DDN+HwLwLFS6EuvLWV3nRdfiCVbGjrjI+bDMZJVQvGUpxP4t2ANtDDCCDdh0erdWiKxEhzjsY630m5S+uXLyQcQpgz59sEQKBgQDe6M3zXQgCKyHdS/xXRsccAmVTmx8EGs5N2KJQ3mEEQipBfoS2iIwSHjp5MxAzfZc2qNVXuSplFTnNUx849OPRpaegF2AtYPDE+SMEURVpy9blRaMJCvW2NoG+Bd/YFkzyd+1wjFrjlOhWTq2x3Z4PasJXF0VvCyT6RROK62NhrwKBgQDdPgJ3BjoYBPvIvE32zvUVZzs9bZhOAYVrvel5/UuqA2Jcb3wMn+aROhbteXEfCuprMVbVPtDSP1PjnTCTOXYzeb6VAlSiplsK5+bgf8Ukepjd9erjEvfe1oX+v3N1dKlthM0Odu16AzVE0hsUgD7ZOmrlM29vny3rKo6wZY+vNQKBgQCbgZ3r4ULtAFonoBLycBYHnEREBzukCW9TL4ZNFFZqOh5wgmrjTG8rVJtGYjEzOrJ7F11+TVceHaitbQejHHSylPZT4PrfrXlKcOPU5xwhKmtIl0Qd0t+Hto7cmgyHG0ei/7dNhR0DF6beTxUtuIXEC554U2vFA+lQD1CK4Zv7UQKBgCR0plVUilgQ4ihGlxY3Lf1T4FgT6gfYilKhnYpDaadfHWMb37X41zZQ+xwwaapy0wPMwoEq85/hXYJhEXMBAfAjfHrzQlz2yoO2iL2vcB29sdjJP+Yg5wUqFjRO+ch967knK97ZS/JR+wIXGO084J5M2PZhjlg3Q4q/nXmFkrKNAoGATf/RigkTwJeQYtmIfkCkByd1601zSz1HftXpQiWC0nrbo6Faw/kK/q/Y94MJdCLYZh6E/EDrrdcYwNBcv7motfrcWRm9c2hkGBuUmEVJIzl/NHpuQIvqW/juFd4fXjBDXtlfiWuO8E4qYKitdXWeQqK/GB0Batb7BUYix2BTQD8=";

        String eMsg = encrypt(publicKey, msg);
        System.out.println("encrypt [" + eMsg + "]");
        String dMsg = decrypt(privateKey, eMsg);
        System.out.println("decrypt [" + dMsg + "]");

    }

    /**
     * 默认 RSA 加密，采用内部初始化的 RSA 密钥对加密
     * 注意：该实例仅在本次 JVM 生命周期有效
     * @param str 要加密的字符串
     * @return 加密后的字符串
     * @throws Exception 加密中的异常
     */
    public static String defaultEncrypt(String str) throws Exception {
        AssertUtil.assertStringNotNull(str);
        final byte[] decoded = Base64.decodeBase64(keyPair.getPublicKeyEncode());
        PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return new String(Base64.encodeBase64(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
    }

    /**
     * 使用指定的 RSA 公钥加密
     * @param publicKey RSA 公钥
     * @param str 要加密文本
     * @return 加密后的文本
     */
    public static String encrypt(PublicKey publicKey, String str) throws Exception {
        AssertUtil.assertStringNotNull(str);
        AssertUtil.assertObjectNotNull(publicKey);
        PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey.getEncoded()));
        // RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return new String(Base64.encodeBase64(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * 使用指定的 RSA 公钥加密
     * @param publicKey RSA 公钥
     * @param encryptContent 要加密文本内容
     * @return 加密后的文本
     */
    public static String publicKeyEncrypt(PublicKey publicKey, String encryptContent) throws Exception {
        AssertUtil.assertStringNotNull(encryptContent);
        AssertUtil.assertObjectNotNull(publicKey);

        // RSA加密
        Cipher cipherEncrypt = Cipher.getInstance("RSA");
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, publicKey);
        return new String(Base64.encodeBase64(cipherEncrypt.doFinal(encryptContent.getBytes(StandardCharsets.UTF_8))),
                StandardCharsets.UTF_8);
    }

    /**
     * 使用指定的 RSA 公钥字符串加密
     * @param publicKey 公钥文本, 公钥经过 base64 加密
     * @param str 要加密文本
     * @return 加密后的文本
     */
    public static String encrypt(String publicKey, String str) throws Exception {
        AssertUtil.assertStringNotNull(str);
        AssertUtil.assertObjectNotNull(publicKey);

        byte[] decoded = Base64.decodeBase64(publicKey);
        PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return new String(Base64.encodeBase64(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * 默认 RSA 解密，使用内部初始化的 RSA 密钥对-私钥解密
     * @param str 要解密的文本
     * @return 解密后的文本
     */
    public static String defaultDecrypt(String str) throws Exception {
        AssertUtil.assertStringNotNull(str);

        // 64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
        // base64 编码的私钥
        byte[] decoded = Base64.decodeBase64(keyPair.getPrivateKeyEncode());
        PrivateKey priKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte), StandardCharsets.UTF_8);
    }

    /**
     * 指定的 RSA 私钥解密
     * @param privateKey 指定解密的 RSA 私钥
     * @param str 要解密的文本
     * @return 解密后的文本
     */
    public static String decrypt(PrivateKey privateKey, String str) throws Exception {
        AssertUtil.assertStringNotNull(str);
        AssertUtil.assertObjectNotNull(privateKey);
        // 64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
        // base64 编码的私钥
        PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKey.getEncoded()));
        // RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte), StandardCharsets.UTF_8);
    }

    /**
     * 指定的 RSA 私钥解密
     * @param privateKey 指定解密的 RSA 私钥
     * @param decryptContent 要解密的文本内容
     * @return 解密后的文本
     */
    public static String privateKeyDecrypt(PrivateKey privateKey, String decryptContent) throws Exception {
        AssertUtil.assertStringNotNull(decryptContent);
        AssertUtil.assertObjectNotNull(privateKey);

        // RSA解密
        Cipher cipherDecrypt = Cipher.getInstance("RSA");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipherDecrypt.doFinal(Base64.decodeBase64(
                decryptContent.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
    }

    /**
     * 指定的 RSA 私钥文本解密
     * @param privateKey 指定解密的 RSA 私钥， 私钥经过 base64 加密
     * @param str 要解密的文本
     * @return 解密后的文本
     */
    public static String decrypt(String privateKey, String str) throws Exception {
        AssertUtil.assertStringNotNull(str);
        AssertUtil.assertObjectNotNull(privateKey);
        // 64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
        // base64 编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte), StandardCharsets.UTF_8);
    }

    /**
     * 产生 RSA 公钥/私钥 密钥对
     * @return RSA 密钥对
     */
    public static KeyPair genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator 类用于生成公钥和私钥对，基于 RSA 算法生成对象
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为 96-1024 位
        keyPairGenerator.initialize(2048);
        // 生成一个密钥对，保存在keyPair中
        return keyPairGenerator.genKeyPair();
    }

    /**
     * 随机生成自定义密钥对实体
     */
    public static RSAKeyPair genRSAKeyPair() throws NoSuchAlgorithmException {
        // 生成一个密钥对，保存在keyPair中
        final KeyPair keyPair = genKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return encodeRSAKeyPair(privateKey, publicKey);
    }

    /**
     * RSA 密钥对进行 base64 加密
     * @param keyPair RSA 密钥对实例
     * @return 自定义的 RSA 密钥对实例
     */
    public static RSAKeyPair encodeRSAKeyPair(KeyPair keyPair) {
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return encodeRSAKeyPair(privateKey, publicKey);
    }

    /**
     * 公钥/私钥 进行 base64 加密
     * @param privateKey 私钥
     * @param publicKey 公钥
     * @return 自定义密钥对实例
     */
    public static RSAKeyPair encodeRSAKeyPair(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        final RSAKeyPair instance = new RSAKeyPair();
        instance.setPrivateKey(privateKey);
        instance.setPublicKey(publicKey);
        instance.setPrivateKeyEncode(new String(Base64.encodeBase64(privateKey.getEncoded())));
        instance.setPublicKeyEncode(new String(Base64.encodeBase64(publicKey.getEncoded())));
        return instance;
    }

    /**
     * RSA 密钥对实体类， 公钥/私钥
     */
    public static class RSAKeyPair {
        /* 公钥 base64 加密 */
        private String publicKeyEncode;
        /* 公钥 */
        private RSAPublicKey publicKey;
        /* 私钥 base64 加密 */
        private String privateKeyEncode;
        /* 私钥 */
        private RSAPrivateKey privateKey;

        public String getPublicKeyEncode() {
            return publicKeyEncode;
        }

        public void setPublicKeyEncode(String publicKeyEncode) {
            this.publicKeyEncode = publicKeyEncode;
        }

        public RSAPublicKey getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(RSAPublicKey publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKeyEncode() {
            return privateKeyEncode;
        }

        public void setPrivateKeyEncode(String privateKeyEncode) {
            this.privateKeyEncode = privateKeyEncode;
        }

        public RSAPrivateKey getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(RSAPrivateKey privateCrtKey) {
            this.privateKey = privateCrtKey;
        }

        @Override
        public String toString() {
            return "RSAKeyPair{" +
                    "publicKeyEncode='" + publicKeyEncode + '\'' +
                    ", privateKeyEncode='" + privateKeyEncode + '\'' +
                    '}';
        }
    }
}
