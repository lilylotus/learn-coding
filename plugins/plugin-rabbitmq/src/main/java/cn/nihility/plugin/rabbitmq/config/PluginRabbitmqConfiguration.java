package cn.nihility.plugin.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

/**
 * 发送消息确认：
 * 用来确认生产者 producer 将消息发送到 broker，broker 上的交换机 exchange 在投递给队列 queue 的过程中，消息是否成功投递.
 * 1. 消息从 producer 到 rabbitmq broker 有一个 confirmCallback 确认模式.
 * 2. 消息从 exchange 到 queue 投递失败有一个 returnCallback 退回模式.
 * 可以利用这两个 Callback 来确保消的 100% 投递.
 *
 * 注意：
 * 1. 开启消息确认机制，消费消息别忘了 channel.basicAck，否则消息会一直存在，导致重复消费.
 * 2. 业务代码一旦出现 bug， 99.9% 的情况是不会自动修复，一条消息会被无限投递进队列，消费端无限执行，导致了死循环.
 *  经过测试分析发现，当消息重新投递到消息队列时，这条消息不会回到队列尾部，仍是在队列头部.
 *  消费者会立刻消费这条消息，业务处理再抛出异常，消息再重新入队，如此反复进行。导致消息队列处理出现阻塞，导致正常消息也无法运行。
 *
 */
public class PluginRabbitmqConfiguration {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(connectionFactory);

        /*
         * 确保消息发送失败后可以重新返回到队列中
         * 注意：yml需要配置 publisher-returns: true
         */
        template.setMandatory(true);
        /*
         * 消费者确认收到消息后，手动 ack 回执回调处理
         */
        template.setConfirmCallback(new ConfirmCallbackServiceImpl());
        /*
         * 消息投递到队列失败回调处理
         */
        template.setReturnsCallback(new ReturnsCallbackServiceImpl());

        return template;
    }

    /**
     * 按照 json 序列化来处理消息
     */
    @Bean
    public RabbitTemplate rabbitTemplateJson(ConnectionFactory connectionFactory) {
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
        rabbitTemplate.setReturnsCallback(new ReturnsCallbackServiceImpl());

        return rabbitTemplate;
    }

    @Bean
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> rabbitListenerContainerFactoryJson(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        // json 序列化时，若想手动 ACK，则必须配置
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    /**
     * 确保消息从 producer 到 rabbitmq broker 发送消息成功。
     * 消息被 broker 接收到只能表示已经到达 MQ 服务器，并不能保证消息一定会被投递到目标 queue
     */
    static class ConfirmCallbackServiceImpl implements RabbitTemplate.ConfirmCallback {

        private static final Logger logger = LoggerFactory.getLogger(ConfirmCallbackServiceImpl.class);

        private String getCorrelationDataId(CorrelationData data) {
            return null == data ? "" : data.getId();
        }

        /**
         * 确认消息是否发送到 rabbitmq broker
         *
         * @param correlationData 对象内部只有一个 id 属性，用来表示当前消息的唯一性
         * @param ack             消息投递到 broker 的状态，true 表示成功
         * @param cause           表示投递失败的原因
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if (ack) {
                logger.info("消息发送到 broker 成功，correlationData [{}], act [{}], cause [{}]",
                    getCorrelationDataId(correlationData), ack, cause);
            } else {
                logger.warn("消息发送到 broker 异常，correlationData [{}], act [{}], cause [{}]",
                    getCorrelationDataId(correlationData), ack, cause);
            }
        }

    }

    /**
     * 如果消息未能由 broker 投递到目标 queue 将触发回调 returnCallback
     * 一旦向 queue 投递消息未成功，一般会记录下当前消息的详细投递数据，方便后续做重发或者补偿等操作
     */
    static class ReturnsCallbackServiceImpl implements RabbitTemplate.ReturnsCallback {

        private static final Logger logger = LoggerFactory.getLogger(ReturnsCallbackServiceImpl.class);

        /**
         * ReturnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey)
         */
        @Override
        public void returnedMessage(ReturnedMessage rm) {
            logger.info("Rabbitmq 的 broker 交换机向 Queue 投递消息失败，响应 message [{}], replyCode [{}], replyText [{}], exchange [{}], routingKey [{}]",
                rm.getMessage(), rm.getReplyCode(), rm.getReplyText(), rm.getExchange(), rm.getRoutingKey());
        }

    }

}
