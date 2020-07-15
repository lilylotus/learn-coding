package cn.nihility.rabbitmq.first;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * Send
 *
 * @author dandelion
 * @date 2020-05-12 15:46
 */
public class Send {

    final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            String message = "Hello World! 你好 Rabbitmq 1";

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null,
                    StandardCharsets.UTF_8.encode(message).array());
            System.out.println("[x] sent [" + message + "]");

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }

    }

}
