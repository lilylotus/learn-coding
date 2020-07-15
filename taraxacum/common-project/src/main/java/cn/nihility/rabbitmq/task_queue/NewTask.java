package cn.nihility.rabbitmq.task_queue;

import cn.nihility.rabbitmq.RabbitmqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * NewTask
 *
 * @author dandelion
 * @date 2020-05-12 16:08
 */
public class NewTask {

    static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) {
        final ConnectionFactory factory = RabbitmqUtil.createFactory();

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

//            String message = String.join("default msg : ", args);
            String message = "task queue publisher";

            channel.basicPublish("", TASK_QUEUE_NAME,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes(StandardCharsets.UTF_8));

            System.out.println("[x] sent message " + message);
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

}
