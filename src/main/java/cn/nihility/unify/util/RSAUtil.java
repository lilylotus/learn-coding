package cn.nihility.unify.util;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String PUBLIC_KEY = "RSAPublicKey";
    public static final String PRIVATE_KEY = "RSAPrivateKey";
    /**
     * 初始化添加随机数
     */
    public static final String KEY_SALT = "SecureRandom";

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
     * @param secret 加密的随机密文
     */
    public static RSAKeyPair initRSAKeyPair(int keyLen, String secret) {
        /* RSA算法要求有一个可信任的随机数源 */
        try {
            //获得对象 KeyPairGenerator 参数 RSA 1024个字节
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom secureRandom = new SecureRandom(secret.getBytes());
            keyPairGen.initialize(keyLen, secureRandom);
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

    public static RSAKeyPair initRSAKeyPair(int keyLen) {
        return initRSAKeyPair(keyLen, KEY_SALT);
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
            synchronized (RSAUtil.class){
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
            synchronized (RSAUtil.class){
                if(localCacheKeyPair == null) {
                    localCacheKeyPair = initRSAKeyPair();
                }
            }
        }
        return localCacheKeyPair;
    }

    /**
     * 生成 RSA 公钥/私钥到指定文件
     * @param privateKeyFilePath 私钥文件
     * @param publicKeyFilePath 公钥文件
     * @param secret 密文，不为空
     * @param keyLen RSA 的长度
     */
    public static void generateRSAKeyPairToFile(String privateKeyFilePath, String publicKeyFilePath, String secret, int keyLen) throws Exception {
        //获得对象 KeyPairGenerator 参数 RSA 1024个字节
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom(secret.getBytes());
        keyPairGen.initialize(keyLen, secureRandom);
        //通过对象 KeyPairGenerator 生成密匙对 KeyPair
        KeyPair keyPair = keyPairGen.generateKeyPair();
        //通过对象 KeyPair 获取RSA公私钥对象 RSAPublicKey RSAPrivateKey
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        byte[] publicKeyEncode = Base64.encodeBase64(publicKey.getEncoded());
        byte[] privateKeyEncode = Base64.encodeBase64(privateKey.getEncoded());

        Files.write(new File(privateKeyFilePath).toPath(), privateKeyEncode);
        Files.write(new File(publicKeyFilePath).toPath(), publicKeyEncode);
    }

    /**
     * 从公钥文件中获取公钥实体
     * @param publicKeyFilePath 公钥文件
     * @return 公钥
     */
    public static PublicKey readPublicKey(String publicKeyFilePath) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(publicKeyFilePath).toPath());
        return getPublicKey(bytes);
    }

    private static PublicKey getPublicKey(byte[] publicKeyByteContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Base64.decodeBase64(publicKeyByteContent);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    /**
     * 从私钥文件中获取公钥实体
     * @param privateKeyFilePath 私钥文件
     * @return 私钥
     */
    public static PrivateKey readPrivateKey(String privateKeyFilePath) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(privateKeyFilePath).toPath());
        return getPrivateKey(bytes);
    }

    private static PrivateKey getPrivateKey(byte[] privateKeyByteContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Base64.decodeBase64(privateKeyByteContent);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }

    static class RSAKeyPair {
        RSAPublicKey rsaPublicKey;
        RSAPrivateKey rsaPrivateKey;

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

    public static void main(String[] args) throws Exception {
        // 使用 java.security.KeyPairGenerator 生成 公/私钥
        /*RSAKeyPair keyPair = initRSAKeyPair();
        String publicKey = getPublicKey(keyPair);
        System.out.println("公钥：\n" + publicKey);
        String privateKey = getPrivateKey(keyPair);
        System.out.println("私钥：\n" + privateKey);*/

        String publicKeyPath = "D:\\id_rsa.pub";
        String privateKeyPath = "D:\\id_rsa";
        String msg = "你好";

        // generateRSAKeyPairToFile(privateKeyPath, publicKeyPath, "secret", 2048);

        PrivateKey privateKey = readPrivateKey(privateKeyPath);
        PublicKey publicKey = readPublicKey(publicKeyPath);

        System.out.println(privateKey);
        System.out.println(publicKey);

        String encrypt = RSAEncryptUtil.publicKeyEncrypt(publicKey, msg);
        String decrypt = RSAEncryptUtil.privateKeyDecrypt(privateKey, encrypt);

        System.out.println(encrypt);
        System.out.println(decrypt);

        // RSA加密
        /*Cipher cipherEncrypt = Cipher.getInstance("RSA");
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, publicKey);
        String encrypt = new String(Base64.encodeBase64(cipherEncrypt.doFinal(msg.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        System.out.println(encrypt);*/

        // RSA解密
        /*Cipher cipherDecrypt = Cipher.getInstance("RSA");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, privateKey);
        String decrypt = new String(cipherDecrypt.doFinal(Base64.decodeBase64(encrypt.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        System.out.println(decrypt);*/
    }
}
