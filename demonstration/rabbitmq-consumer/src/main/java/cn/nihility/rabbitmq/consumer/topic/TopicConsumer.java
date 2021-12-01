package cn.nihility.rabbitmq.consumer.topic;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class TopicConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TopicConsumer.class);

    @RabbitListener(queues = {TopicConfiguration.TOPIC_QUEUE_A})
    public void consumerA(Map<String, Object> data, Channel channel, Message message) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        logger.info("消费 Topic A 队列消息 DeliveryTag [{}] : [{}]", tag, data);
        channel.basicAck(tag, false);
    }

    @RabbitListener(queues = {TopicConfiguration.TOPIC_QUEUE_B})
    public void consumerB(Map<String, Object> data, Channel channel, Message message) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        logger.info("消费 Topic B 队列消息 DeliveryTag [{}] : [{}]", tag, data);
        channel.basicAck(tag, false);
    }

}
