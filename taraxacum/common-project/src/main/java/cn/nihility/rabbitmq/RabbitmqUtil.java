package cn.nihility.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * RabbitmqUtil
 *
 * @author dandelion
 * @date 2020-05-12 16:09
 */
public class RabbitmqUtil {

    public static ConnectionFactory createFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        return factory;
    }

    public static byte[] encode(String msg) {
        return StandardCharsets.UTF_8.encode(msg).array();
    }

    public static String decode(byte[] msg) {
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(msg)).toString();
    }

}
