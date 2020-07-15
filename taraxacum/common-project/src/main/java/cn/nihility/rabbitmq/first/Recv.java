package cn.nihility.rabbitmq.first;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * Recv
 *
 * @author dandelion
 * @date 2020-05-12 15:55
 */
public class Recv {

    static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("[x] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            final String msg = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(message.getBody())).toString();
            System.out.println("[x] received msg " + msg);
        };

        /* autoAck -> true 服务端当发送完后就结束了，不用在询问客户端是否确实收到信息 */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback,
                consumerTag -> System.out.println("CancelCallback"));

    }

}
