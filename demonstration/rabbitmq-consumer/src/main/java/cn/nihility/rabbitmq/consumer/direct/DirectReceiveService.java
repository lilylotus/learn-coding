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

}
