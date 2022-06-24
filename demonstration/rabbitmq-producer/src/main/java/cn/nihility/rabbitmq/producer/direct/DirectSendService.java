package cn.nihility.rabbitmq.producer.direct;

import cn.nihility.rabbitmq.producer.delay.DelayedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
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
    private static final Logger logger = LoggerFactory.getLogger(DirectSendService.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitTemplate rabbitTemplateJson;
    private final RabbitTemplate rabbitTemplateJdbc;

    public DirectSendService(RabbitTemplate rabbitTemplate,
                             RabbitTemplate rabbitTemplateJson,
                             RabbitTemplate rabbitTemplateJdbc) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplateJson = rabbitTemplateJson;
        this.rabbitTemplateJdbc = rabbitTemplateJdbc;
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

    /* ---------- rabbitmq 保证消息不丢失 ---------- */

    private Map<String, Object> createJdbcData(String id, String msg) {
        Map<String, Object> data = new HashMap<>(8);
        data.put("id", id);
        data.put("message", msg);
        data.put("createTime", DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        return data;
    }

    public void sendJdbcMessage(String exchange, String routeKey) {
        String id = randomUUID();
        Map<String, Object> data = createJdbcData(id, "发送 JdbcMessage 到 Rabbitmq [" + id + "]");

        /*MessageProperties properties = MessagePropertiesBuilder.newInstance()
            .setContentEncoding("UTF-8")
            .setContentType(MessageProperties.CONTENT_TYPE_JSON)
            .setHeader("hello", "helloValue")
            .setCorrelationId(id)
            .build();

        Message message = MessageBuilder.withBody(dataString.getBytes(StandardCharsets.UTF_8))
            .andProperties(properties)
            .build();

        CorrelationData correlationData = new CorrelationData(id);
        correlationData.setReturnedMessage(message);*/

        rabbitTemplateJdbc.convertAndSend(exchange, routeKey, data, new CorrelationData(id));
    }

    /* ------ delayed ------*/

    public void sendDelayedMessage(final int delaySeconds) {
        String id = UUID.randomUUID().toString().replace("-", "");
        String message = "test message, hello! delayed [" + delaySeconds + "] [" + id + "]";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> data = new HashMap<>(4);
        data.put("messageId", id);
        data.put("messageData", message);
        data.put("createTime", createTime);
        data.put("delaySeconds", delaySeconds);

        rabbitTemplate.convertAndSend(DelayedConfiguration.DELAYED_EXCHANGE,
            DelayedConfiguration.DELAYED_ROUTE_KEY, data, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties properties = message.getMessageProperties();
                    //设置消息持久化
                    //properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    // 设置延时
                    properties.setDelay(delaySeconds * 1000);
                    //properties.setHeader("x-delay", delaySeconds);
                    return message;
                }
            }, new CorrelationData(id));
    }

}
