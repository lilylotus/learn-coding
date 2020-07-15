package cn.nihility.boot.util;

import java.io.*;

/**
 * 序列化工具
 * @author muscari
 * @date 2019-07-10 23:53
 */
public class SerializeUtil {

    public static byte[] serialize(Object object) {
        ObjectOutputStream objectOutputStream;
        ByteArrayOutputStream byteArrayOutputStream;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object deSerialize(byte[] bytes) {
        ObjectInputStream objectInputStream;
        ByteArrayInputStream byteArrayInputStream;

        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);

            return objectInputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
