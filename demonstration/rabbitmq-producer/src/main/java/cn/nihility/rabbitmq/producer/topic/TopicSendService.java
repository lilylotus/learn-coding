package cn.nihility.rabbitmq.producer.topic;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TopicSendService {

    private RabbitTemplate rabbitTemplate;

    public TopicSendService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void topicASend() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", UUID.randomUUID().toString());
        dataMap.put("message", "Topic A Message");
        dataMap.put("createTime", LocalDateTime.now());
        rabbitTemplate.convertAndSend(TopicConfiguration.TOPIC_EXCHANGE, TopicConfiguration.TOPIC_QUEUE_A,
            dataMap, new CorrelationData(UUID.randomUUID().toString().replace("-", "")));
    }

    public void topicBSend() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", UUID.randomUUID().toString());
        data.put("message", "Topic All Queue Message.");
        data.put("createTime", LocalDateTime.now());
        rabbitTemplate.convertAndSend(TopicConfiguration.TOPIC_EXCHANGE, TopicConfiguration.TOPIC_QUEUE_B,
            data, new CorrelationData(UUID.randomUUID().toString().replace("-", "")));
    }

}
