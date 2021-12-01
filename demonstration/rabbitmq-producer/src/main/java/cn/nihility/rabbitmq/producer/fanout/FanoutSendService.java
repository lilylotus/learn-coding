package cn.nihility.rabbitmq.producer.fanout;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FanoutSendService {

    private RabbitTemplate rabbitTemplate;

    public FanoutSendService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void fanoutSend() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("messageId", UUID.randomUUID().toString());
        dataMap.put("messageData", "Fanout Exchange Bing Queue Data Message");
        dataMap.put("createTime", LocalDateTime.now());
        rabbitTemplate.convertAndSend(FanoutConfiguration.FANOUT_EXCHANGE, "",
            dataMap, new CorrelationData(UUID.randomUUID().toString().replace("-", "")));
    }

}
