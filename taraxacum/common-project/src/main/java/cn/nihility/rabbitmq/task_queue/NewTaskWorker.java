package cn.nihility.rabbitmq.task_queue;

import cn.nihility.rabbitmq.RabbitmqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * NewTaskWorker
 *
 * @author dandelion
 * @date 2020-05-12 16:15
 */
public class NewTaskWorker {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        final ConnectionFactory factory = RabbitmqUtil.createFactory();

        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println("[x] Waiting for message.");

        channel.basicQos(1);

        DeliverCallback deliverCallback = ((consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println("[x] Received " + msg);

            /* 注意，这里要确认回复 */
            try {
                doWork(msg);
            } finally {
                System.out.println("[x] done.");
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
        });

        /* autoAck -> false 服务端当发送完后要等待询问客户端是否确实收到信息 */
        channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback,
                consumerTag -> System.out.println("ConsumerTag"));
    }

    private static void doWork(String message) {
        for (char ch : message.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
