package cn.nihility.rabbitmq.publish_subscribe;

import cn.nihility.rabbitmq.RabbitmqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

/**
 * EmitLogFanout
 *
 * @author dandelion
 * @date 2020-05-12 16:38
 */
public class EmitLog {

    private static final String EXCHANGE_NAME = "fanout_logs";
    private static final String EXCHANGE_DIRECT_NAME = "direct_logs";
    private static final String EXCHANGE_TOPIC_NAME = "topic_logs";

    public static void main(String[] args) {
        String message = "Hello Rabbitmq";
        final ConnectionFactory factory = RabbitmqUtil.createFactory();

        String type = "fanout";
        if (args.length == 1) {
            type = args[0];
        } else if (args.length == 2) {
            type = args[0];
            message = args[1];
        }

        if ("fanout".equalsIgnoreCase(type)) {
            fanout(factory, message);
        } else if ("direct".equalsIgnoreCase(type)) {
            direct(factory, message);
        } else if ("topic".equalsIgnoreCase(type)) {
            topic(factory, message);
        }

    }

    public static void fanout(ConnectionFactory factory, final String message) {
        BiConsumer<Channel, String> action = (channel, msg) -> {
            String send = "fanout -> " + message;
            try {
                /* 采用 fanout 方式的 exchange ，类似广播方式 */
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
                channel.basicPublish(EXCHANGE_NAME, "", null, RabbitmqUtil.encode(send));
                System.out.println("[x] fanout sent " + send);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        basicOperation(factory, message, action);
    }

    public static void direct(ConnectionFactory factory, final String message) {
        BiConsumer<Channel, String> action = (channel, msg) -> {
            String send = "direct -> " + message;
            String severity = "warning";
            try {
                /* 采用 direct 方式的 exchange */
                channel.exchangeDeclare(EXCHANGE_DIRECT_NAME, BuiltinExchangeType.DIRECT);
                /* 指定路由的 key */
                channel.basicPublish(EXCHANGE_DIRECT_NAME, severity, null, RabbitmqUtil.encode(send));
                System.out.println("[x] direct sent severity " + severity + " : " + send);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        basicOperation(factory, message, action);
    }

    public static void topic(ConnectionFactory factory, final String message) {
        BiConsumer<Channel, String> action = (channel, msg) -> {
            String send = "topic -> " + message;
            String routingKey = "anonymous.info";
            try {
                /* 采用 topic 方式的 exchange */
                channel.exchangeDeclare(EXCHANGE_TOPIC_NAME, BuiltinExchangeType.TOPIC);
                /* 指定路由的 key */
                channel.basicPublish(EXCHANGE_TOPIC_NAME, routingKey, null, RabbitmqUtil.encode(send));
                System.out.println("[x] direct sent routingKey " + routingKey + " : " + send);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        basicOperation(factory, message, action);
    }

    private static void basicOperation(ConnectionFactory factory, final String message,
                                       BiConsumer<Channel, String> action) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            action.accept(channel, message);
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

}
