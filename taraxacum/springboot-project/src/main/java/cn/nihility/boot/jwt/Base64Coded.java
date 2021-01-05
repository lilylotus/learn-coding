package cn.nihility.boot.jwt;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

public class Base64Coded {

    /**
     * base64 解码
     * @param bytes 要解码的字节数组
     * @return 解码后的字节数组字符串
     */
    public static String decode(byte[] bytes) {
        return new String(Base64.decodeBase64(bytes), StandardCharsets.UTF_8);
    }

    /**
     * base64 编码
     * @param bytes 要编码的字节数组
     * @return 编码后的字节数组字符串
     */
    public static String encode(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8);
    }

    /**
     * 把编码后的 base64 字符串解码为字节数组
     * @param encrypt 编码后的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decode(String encrypt) {
        return Base64.decodeBase64(encrypt);
    }

    public static String encodeBase64String(byte[] decrypt) {
        return Base64.encodeBase64String(decrypt);
    }

}
