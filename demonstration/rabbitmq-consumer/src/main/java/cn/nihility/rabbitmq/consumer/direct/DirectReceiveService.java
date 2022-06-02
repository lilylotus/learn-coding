package cn.nihility.rabbitmq.consumer.direct;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DirectReceiveService {

    private static final Logger logger = LoggerFactory.getLogger(DirectReceiveService.class);

    /* ---------- 普通队列/交换机 ---------- */
    @RabbitListener(queues = {DirectConfiguration.NORMAL_DIRECT_QUEUE})
    public void normalDirectQueueReceiver(Map<String, Object> dataMap, Message message, Channel channel) throws IOException {

        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        final Boolean redelivered = message.getMessageProperties().getRedelivered();
        logger.info("收到 [{}] 消息 deliveryTag [{}], redelivered [{}] : [{}]",
            DirectConfiguration.NORMAL_DIRECT_QUEUE, deliveryTag, redelivered, dataMap);

        try {
            // 确认处理了该消息
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            logger.error("确认收到 [{}] 队列消息异常", DirectConfiguration.NORMAL_DIRECT_QUEUE, e);
            if (Boolean.TRUE.equals(redelivered)) {
                logger.error("消息已重复处理失败,拒绝再次接收...");
                // requeue - 是否从入队列
                channel.basicReject(deliveryTag, true);
            } else {
                logger.error("消息即将再次返回队列处理...");
                channel.basicNack(deliveryTag, false, true);
            }
        }

        // 1. 当消费失败后将此消息存到 Redis，记录消费次数，如果消费了三次还是失败，就丢弃掉消息，记录日志落库保存
        // 2. 直接填 false ，不重回队列，记录日志、发送邮件等待开发手动处理
        // 3. 不启用手动 ack ，使用 SpringBoot 提供的消息重试
        // 注意：一定要手动 throw 一个异常 （RuntimeException），因为 SpringBoot 触发重试是根据方法中发生未捕捉的异常来决定的。
        // 这个重试是 SpringBoot 提供的，重新执行消费者方法，而不是让 RabbitMQ 重新推送消息。

    }

    /* ---------- 普通 JSON 队列/交换机 ---------- */
    @RabbitListener(queues = {DirectConfiguration.NORMAL_DIRECT_JSON_QUEUE},
        containerFactory = "rabbitListenerContainerFactoryJson")
    public void normalDirectJsonQueueReceiver(@Payload Map<String, Object> dataMap,
                                              Message message, Channel channel) throws IOException {
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        logger.info("normalJsonDirectQueue receiver deliveryTag [{}] : [{}]", deliveryTag, dataMap);

        if (deliveryTag % 3 == 0) {
            logger.error("消费消息发生异常 basicNack requeue = false - tag [{}]", deliveryTag);
            channel.basicNack(deliveryTag, false, false);
        } else if (deliveryTag % 3 == 1) {
            logger.error("消费消息发生异常 basicNack requeue = true - tag [{}]", deliveryTag);
            channel.basicNack(deliveryTag, false, true);
        } else {
            logger.info("消费消息确认 tag [{}]", deliveryTag);
            channel.basicAck(deliveryTag, false);
        }
    }

    /**
     * 死信队列消费
     */
    @RabbitListener(queues = {DirectConfiguration.DIRECT_DEAD_LETTER_QUEUE})
    public void deadLetterQueueReceiver(Map<String, Object> dataMap, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        logger.info("消费死信队列消息 DeliveryTag [{}] : [{}]", tag, dataMap);
        channel.basicAck(tag, false);
    }

    /**
     * 业务消息队列信息消费
     */
    @RabbitListener(queues = {DirectConfiguration.DIRECT_BUSINESS_QUEUE_WITH_DEAD_LETTER})
    public void businessQueueReceiver(Map<String, Object> dataMap, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        logger.info("消费业务消息队列消息 DeliveryTag [{}] : [{}]", deliveryTag, dataMap);
        if (deliveryTag % 5 == 0) {
            logger.error("消费业务消息发生异常 basicNack requeue = false - tag [{}]", deliveryTag);
            channel.basicNack(deliveryTag, false, false);
        } else if (deliveryTag % 5 == 2) {
            logger.error("消费业务消息发生异常 basicNack requeue = true - tag [{}]", deliveryTag);
            channel.basicNack(deliveryTag, false, true);
        } else {
            logger.info("消费业务消息确认 tag [{}]", deliveryTag);
            channel.basicAck(deliveryTag, false);
        }
    }

    private Map<String, Integer> consumerMsgCount = new ConcurrentHashMap<>();

    @RabbitListener(queues = {DirectConfiguration.JDBC_DIRECT_QUEUE},
        containerFactory = "rabbitListenerContainerFactoryJson")
    public void businessJdbcQueueReceiver(Map<String, Object> dataMap, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String id = Objects.toString(dataMap.get("id"), null);

        try {
            if (id == null) {
                logger.error("消费 JdbcQueue 消息异常, 数据不正确, basicNack requeue = false - tag [{}], data [{}]",
                    deliveryTag, dataMap);
                channel.basicNack(deliveryTag, false, false);
            } else if (id.contains("6") || id.contains("a")) {
                Integer cnt = consumerMsgCount.getOrDefault(id, 1);
                if (cnt > 3) {
                    logger.error("模拟多次消费消息异常，入库记录，丢弃改消息 basicNack requeue = false - tag [{}], data [{}]", deliveryTag, dataMap);
                    channel.basicNack(deliveryTag, false, false);
                    consumerMsgCount.remove(id);
                } else {
                    logger.error("消费 JdbcQueue 消息异常, 处理数据失败, basicNack requeue = true - tag [{}], data [{}]",
                        deliveryTag, dataMap);
                    channel.basicNack(deliveryTag, false, true);
                    consumerMsgCount.put(id, cnt + 1);
                }
            } else {
                logger.info("正确消费数据 [{}]", deliveryTag);
                channel.basicAck(deliveryTag, false);
            }
        } catch (IOException e) {
            logger.error("消费 JdbcQueue 消息异常", e);
        }
    }

    @RabbitListener(queues = {DirectConfiguration.JDBC_DEAD_LETTER_DIRECT_QUEUE},
        containerFactory = "rabbitListenerContainerFactoryJson")
    public void businessJdbcDeadQueueReceiver(Map<String, Object> dataMap, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        logger.info("jdbcDeadLetterQueue -> deliveryTag [{}], [{}]", deliveryTag, dataMap);

        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            logger.error("消费 JdbcDeadQueue 消息异常", e);
        }
    }

}
