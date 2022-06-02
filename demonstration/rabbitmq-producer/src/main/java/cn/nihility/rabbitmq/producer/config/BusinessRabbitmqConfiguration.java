package cn.nihility.rabbitmq.producer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
public class BusinessRabbitmqConfiguration {

    /**
     * 标准业务逻辑，使用 json 序列化来处理消息
     */
    @Bean
    public RabbitTemplate rabbitTemplateJdbc(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //设置开启 Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);

        /* 使用 json 转换消息 */
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        /*
         * 消费者确认收到消息后，手动 ack 回执回调处理
         */
        rabbitTemplate.setConfirmCallback(new ConfirmCallbackServiceImpl());
        /*
         * 消息投递到队列失败回调处理
         */
        rabbitTemplate.setReturnCallback(new ReturnsCallbackServiceImpl());

        return rabbitTemplate;
    }

    private static String parseMessageProperties(MessageProperties pro) {
        if (null == pro) {
            return "";
        }
        String exchange = pro.getReceivedExchange();
        String routingKey = pro.getReceivedRoutingKey();
        String queue = pro.getConsumerQueue();
        long deliveryTag = pro.getDeliveryTag();
        String correlationId = pro.getCorrelationId();

        return String.format("(correlationId[%s], exchange[%s], routingKey[%s], queue[%s], deliveryTag[%d])",
            correlationId, exchange, routingKey, queue, deliveryTag);
    }

    /**
     * confirm 监听，当消息成功发到交换机 ack = true，没有发送到交换机 ack = false
     */
    private static class ConfirmCallbackServiceImpl implements RabbitTemplate.ConfirmCallback {

        private static final Logger logger = LoggerFactory.getLogger(ConfirmCallbackServiceImpl.class);

        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            String id = correlationData.getId();
            MessageProperties properties = Optional.ofNullable(correlationData.getReturnedMessage()).map(Message::getMessageProperties).orElse(null);
            if (ack) {
                logger.info("消息 -> broker(exchange) [success], id [{}]", id);
            } else {
                logger.error("模拟消息发送失败入库，消息 -> broker(exchange) [failure], id [{}] [{}], case [{}]",
                    id, parseMessageProperties(properties), cause);
            }
        }
    }

    /**
     * 消息未能投递到目标 queue 将触发回调 returnCallback
     */
    private static class ReturnsCallbackServiceImpl implements RabbitTemplate.ReturnCallback {

        private static final Logger logger = LoggerFactory.getLogger(ReturnsCallbackServiceImpl.class);

        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            MessageProperties messageProperties = message.getMessageProperties();
            String msg = parseMessageProperties(messageProperties);
            String messageBody = new String(message.getBody());
            String id = Objects.toString(messageProperties.getHeader("spring_returned_message_correlation"), null);
            logger.error("模拟消息发送失败入库，消息 -> queue [failure], id [{}], content [{}] replyCode [{}], " +
                    "replyText [{}], exchange [{}], routingKey [{}], Message [{}]",
                id, messageBody, replyCode, replyText, exchange, routingKey, msg);

        }
    }
}
