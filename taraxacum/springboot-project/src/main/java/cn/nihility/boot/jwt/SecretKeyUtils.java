package cn.nihility.boot.jwt;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class SecretKeyUtils {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String PUBLIC_KEY = "RSAPublicKey";
    public static final String PRIVATE_KEY = "RSAPrivateKey";

    private static RSAKeyPair rsa256Key;
    private static RSAKeyPair localCacheKeyPair;

    /**
     * 获得公钥
     */
    public static String getPublicKey(RSAKeyPair rsaKeyPair) {
        if (null == rsaKeyPair) return null;
        //编码返回字符串
        return rsaKeyPair.getPublicKeyBase64Encode();
    }

    /**
     * 获得公钥
     */
    public static String getPublicKey() {
        return getPublicKey(localCacheKeyPair());
    }

    /**
     * 获得私钥
     */
    public static String getPrivateKey(RSAKeyPair rsaKeyPair) {
        if (null == rsaKeyPair) return null;
        return rsaKeyPair.getPrivateKeyBase64Encode();
    }

    /**
     * 获得私钥
     */
    public static String getPrivateKey() {
        return getPrivateKey(localCacheKeyPair());
    }

    /**
     * 使用 KeyPairGenerator 生成公私钥，存放于 RSAKeyPair 对象中
     * @param keyLen RSA 的长度
     */
    public static RSAKeyPair initRSAKeyPair(int keyLen) {
        /* RSA算法要求有一个可信任的随机数源 */
        try {
            //获得对象 KeyPairGenerator 参数 RSA 1024个字节
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(keyLen);
            //通过对象 KeyPairGenerator 生成密匙对 KeyPair
            KeyPair keyPair = keyPairGen.generateKeyPair();
            //通过对象 KeyPair 获取RSA公私钥对象 RSAPublicKey RSAPrivateKey
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            // 公私钥对象存入 RAS KeyPair 对象
            return new RSAKeyPair(publicKey, privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用 KeyPairGenerator 生成公私钥，存放于 RSAKeyPair 对象中
     * 生成 RSA 的长度为 2048
     */
    public static RSAKeyPair initRSAKeyPair() {
        return initRSAKeyPair(2048);
    }

    /**
     * 获取公私钥
     */
    public static synchronized RSAKeyPair getRSA256KeyPair() {
        if(rsa256Key == null){
            synchronized (SecretKeyUtils.class){
                if(rsa256Key == null) {
                    rsa256Key = initRSAKeyPair();
                }
            }
        }
        return rsa256Key;
    }

    /**
     * 获取本类缓存公私钥
     */
    private static synchronized RSAKeyPair localCacheKeyPair() {
        if(localCacheKeyPair == null){
            synchronized (SecretKeyUtils.class){
                if(localCacheKeyPair == null) {
                    localCacheKeyPair = initRSAKeyPair();
                }
            }
        }
        return localCacheKeyPair;
    }

    static class RSAKeyPair {
        RSAPublicKey rsaPublicKey;
        RSAPrivateKey rsaPrivateKey;

        public RSAKeyPair() {
        }

        public RSAKeyPair(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
            this.rsaPublicKey = rsaPublicKey;
            this.rsaPrivateKey = rsaPrivateKey;
        }

        public String getPublicKeyBase64Encode() {
            return Base64.encodeBase64String(rsaPublicKey.getEncoded());
        }

        public byte[] getPublicKeyBase64Decode(String publicKeyBase64Encode) {
            return Base64.decodeBase64(publicKeyBase64Encode);
        }

        public String getPrivateKeyBase64Encode() {
            return Base64.encodeBase64String(rsaPrivateKey.getEncoded());
        }

        public byte[] getPrivateKeyKeyBase64Decode(String privateKeyKeyBase64Encode) {
            return Base64.decodeBase64(privateKeyKeyBase64Encode);
        }

        public RSAPublicKey getRsaPublicKey() {
            return rsaPublicKey;
        }

        public RSAPrivateKey getRsaPrivateKey() {
            return rsaPrivateKey;
        }
    }

    public static void main(String[] args) {
        // 使用 java.security.KeyPairGenerator 生成 公/私钥
        RSAKeyPair keyPair = initRSAKeyPair();
        String publicKey = getPublicKey(keyPair);
        System.out.println("公钥：\n" + publicKey);
        String privateKey = getPrivateKey(keyPair);
        System.out.println("私钥：\n" + privateKey);
    }
}
