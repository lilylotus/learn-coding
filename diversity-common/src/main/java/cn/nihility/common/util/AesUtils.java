package cn.nihility.common.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AesUtils {

    public static final String STRING_AES = "AES";
    public static final String AES_CIPHER_INSTANCE = "AES/ECB/PKCS5Padding";

    private AesUtils() {
    }

    /**
     * 生成随机密钥
     *
     * @param keySize 密钥大小推荐 128 256
     * @return 随机密钥
     * @throws NoSuchAlgorithmException 算法不存在异常
     */
    public static String generateSecret(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance(STRING_AES);
        generator.init(keySize, new SecureRandom());
        SecretKey key = generator.generateKey();
        return Hex.encodeHexString(key.getEncoded());
    }

    public static String encrypt(String strToEncrypt, String secret) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec secretKey = getKey(secret);
        Cipher cipher = Cipher.getInstance(AES_CIPHER_INSTANCE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
    }

    public static String decrypt(String strToDecrypt, String secret) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec secretKey = getKey(secret);
        Cipher cipher = Cipher.getInstance(AES_CIPHER_INSTANCE);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }

    private static SecretKeySpec getKey(String myKey) throws NoSuchAlgorithmException {
        byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, STRING_AES);
    }

    /**
     * byte数组转化为16进制字符串
     */
    private static String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String strHex = Integer.toHexString(aByte);
            if (strHex.length() > 3) {
                sb.append(strHex.substring(6));
            } else {
                if (strHex.length() < 2) {
                    sb.append("0").append(strHex);
                } else {
                    sb.append(strHex);
                }
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        String secret = generateSecret(128);
        String str = "加密字符串";
        String encrypt = encrypt(str, secret);
        System.out.println(encrypt);
        String decrypt = decrypt(encrypt, secret);
        System.out.println(decrypt);
    }

}
