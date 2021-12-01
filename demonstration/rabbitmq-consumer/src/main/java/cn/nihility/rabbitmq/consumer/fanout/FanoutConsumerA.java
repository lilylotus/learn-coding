package cn.nihility.rabbitmq.consumer.fanout;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RabbitListener(queues = {FanoutConfiguration.FANOUT_QUEUE_A})
public class FanoutConsumerA {

    private static final Logger logger = LoggerFactory.getLogger(FanoutConsumerA.class);

    @RabbitHandler
    public void handler(Map<String, Object> data, Channel channel, Message message) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        logger.info("消费 Fanout A 队列消息 DeliveryTag [{}] : [{}]", tag, data);
        channel.basicAck(tag, false);
    }

}
