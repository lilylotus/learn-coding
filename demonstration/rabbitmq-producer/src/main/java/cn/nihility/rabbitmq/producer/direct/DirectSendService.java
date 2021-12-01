package cn.nihility.rabbitmq.producer.direct;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DirectSendService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private RabbitTemplate rabbitTemplate;
    private RabbitTemplate rabbitTemplateJson;

    public DirectSendService(RabbitTemplate rabbitTemplate,
                             RabbitTemplate rabbitTemplateJson) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplateJson = rabbitTemplateJson;
    }

    String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    Map<String, Object> createData(String msg) {
        Map<String, Object> data = new HashMap<>(8);
        String uuid = randomUUID();
        data.put("id", uuid);
        data.put("message", msg);
        data.put("createTime", DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        return data;
    }

    /* ---------- 普通队列/交换机 ---------- */

    public void sendNormalDirectQueueWithCorrelationData() {
        // new CorrelationData(UUID.randomUUID().toString()) 指定消息的唯一 id
        // 会将消息的唯一 id 存入数据库中，用作后期队列中的消息消费情况做对比
        rabbitTemplate.convertAndSend(DirectConfiguration.NORMAL_DIRECT_EXCHANGE,
            DirectConfiguration.NORMAL_DIRECT_EXCHANGE_BIND_QUEUE_KEY,
            createData("Direct Exchange Bing Queue With CorrelationData"),
            new CorrelationData(randomUUID()));
    }

    public void sendNormalDirectQueue() {
        // new CorrelationData(UUID.randomUUID().toString()) 指定消息的唯一 id
        // 会将消息的唯一 id 存入数据库中，用作后期队列中的消息消费情况做对比
        rabbitTemplate.convertAndSend(DirectConfiguration.NORMAL_DIRECT_EXCHANGE,
            DirectConfiguration.NORMAL_DIRECT_EXCHANGE_BIND_QUEUE_KEY,
            createData("Direct Exchange Bing Queue"));
    }

    public void sendNormalDirectQueueUseJsonTemplate() {
        // new CorrelationData(UUID.randomUUID().toString()) 指定消息的唯一 id
        // 会将消息的唯一 id 存入数据库中，用作后期队列中的消息消费情况做对比
        rabbitTemplateJson.convertAndSend(DirectConfiguration.NORMAL_DIRECT_EXCHANGE,
            DirectConfiguration.NORMAL_DIRECT_EXCHANGE_BIND_JSON_QUEUE_KEY,
            createData("Direct Exchange Bing Queue Json Data"),
            new CorrelationData(randomUUID()));
    }

    /* ---------- 定义业务队列绑定到死信队列/交换机 ---------- */
    public void sendBusinessDirectQueue() {
        // new CorrelationData(UUID.randomUUID().toString()) 指定消息的唯一 id
        // 会将消息的唯一 id 存入数据库中，用作后期队列中的消息消费情况做对比
        rabbitTemplate.convertAndSend(DirectConfiguration.NORMAL_DIRECT_EXCHANGE,
            DirectConfiguration.DIRECT_EXCHANGE_BIND_DIRECT_BUSINESS_QUEUE_KEY,
            createData("Direct Exchange Bing Business Queue Send Data"),
            new CorrelationData(randomUUID()));
    }

    public void sendLonelyExchangeWithQueue() {
        // new CorrelationData(UUID.randomUUID().toString()) 指定消息的唯一 id
        // 会将消息的唯一 id 存入数据库中，用作后期队列中的消息消费情况做对比
        rabbitTemplate.convertAndSend(DirectConfiguration.DIRECT_LONELY_EXCHANGE,
            DirectConfiguration.DIRECT_EXCHANGE_BIND_DIRECT_BUSINESS_QUEUE_KEY,
            createData("sendLonelyExchangeWithQueue Send Data"),
            new CorrelationData(randomUUID()));
    }

}
