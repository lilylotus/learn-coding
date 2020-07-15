package cn.nihility.rabbitmq.publish_subscribe;

import cn.nihility.rabbitmq.RabbitmqUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * ReceiveLog
 *
 * @author dandelion
 * @date 2020-05-12 17:11
 */
public class ReceiveLog {

    private static final String EXCHANGE_FANOUT_NAME = "fanout_logs";
    private static final String EXCHANGE_DIRECT_NAME = "direct_logs";
    private static final String EXCHANGE_TOPIC_NAME = "topic_logs";

    public static void main(String[] args) {
        final ConnectionFactory factory = RabbitmqUtil.createFactory();

        String type = "fanout";
        if (args.length == 1) {
            type = args[0];
        }

        if ("fanout".equalsIgnoreCase(type)) {
            fanout(factory);
        } else if ("direct".equalsIgnoreCase(type)) {
            direct(factory);
        } else if ("topic".equalsIgnoreCase(type)) {
            topic(factory);
        }
    }

    public static void fanout(ConnectionFactory factory) {
        Consumer<Channel> action = channel -> {
            try {
                channel.exchangeDeclare(EXCHANGE_FANOUT_NAME, BuiltinExchangeType.FANOUT);
                /* 随机产生一个 Queue 的名称 */
                final String queueName = channel.queueDeclare().getQueue();
                System.out.println("fanout get queue name " + queueName);
                channel.queueBind(queueName, EXCHANGE_FANOUT_NAME, "");
                System.out.println("[x] Waiting for message.");

                DeliverCallback deliverCallback = ((consumerTag, message) -> {
                    final String msg = RabbitmqUtil.decode(message.getBody());
                    System.out.println("[x] Received " + msg);
                });

                channel.basicConsume(queueName, true, deliverCallback,
                        consumerTag -> System.out.println("consumerTag"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        basicOperation(factory, action);
    }

    public static void direct(ConnectionFactory factory) {
        Consumer<Channel> action = channel -> {
            try {
                channel.exchangeDeclare(EXCHANGE_DIRECT_NAME, BuiltinExchangeType.DIRECT);
                /* 随机产生一个 Queue 的名称 */
                final String queueName = channel.queueDeclare().getQueue();
                System.out.println("direct get queue name " + queueName);

                String[] severity = new String[] {"info", "warning", "error"};
                /* consumer 可以和多个 routingKey 绑定 */
                for (String s : severity) {
                    channel.queueBind(queueName, EXCHANGE_DIRECT_NAME, s);
                }
                System.out.println("[x] Waiting for message.");

                DeliverCallback deliverCallback = ((consumerTag, message) -> {
                    final String msg = RabbitmqUtil.decode(message.getBody());
                    System.out.println("[x] Received " + message.getEnvelope().getRoutingKey() + " : " + msg);
                });

                channel.basicConsume(queueName, true, deliverCallback,
                        consumerTag -> System.out.println("consumerTag"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        basicOperation(factory, action);
    }

    public static void topic(ConnectionFactory factory) {
        Consumer<Channel> action = channel -> {
            try {
                channel.exchangeDeclare(EXCHANGE_TOPIC_NAME, BuiltinExchangeType.TOPIC);
                /* 随机产生一个 Queue 的名称 */
                final String queueName = channel.queueDeclare().getQueue();
                System.out.println("topic get queue name " + queueName);

                String[] severity = new String[] {"anonymous.info"};
                /* consumer 可以和多个 routingKey 绑定 */
                for (String bindingKey : severity) {
                    channel.queueBind(queueName, EXCHANGE_TOPIC_NAME, bindingKey);
                }
                System.out.println("[x] Waiting for message.");

                DeliverCallback deliverCallback = ((consumerTag, message) -> {
                    final String msg = RabbitmqUtil.decode(message.getBody());
                    System.out.println("[x] Received " + message.getEnvelope().getRoutingKey() + " : " + msg);
                });

                channel.basicConsume(queueName, true, deliverCallback,
                        consumerTag -> System.out.println("consumerTag"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        basicOperation(factory, action);
    }

    private static void basicOperation(ConnectionFactory factory,
                                       Consumer<Channel> action) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            action.accept(channel);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}
