package cn.nihility.rabbitmq.producer.topic;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 订阅主题类型交换机
 *
 *                       -- route(*.key.*) --> queue1
 * P --> topic_exchange  -- route(*.*.key) --> queue2
 *                       -- route(key.#) --> queue3
 */
@Configuration(proxyBeanMethods = false)
public class TopicConfiguration {

    public static final String TOPIC_EXCHANGE = "TopicExchange";
    public static final String TOPIC_QUEUE_A = "Topic.A";
    public static final String TOPIC_QUEUE_B = "Topic.B";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Queue topicQueueA() {
        return new Queue(TOPIC_QUEUE_A);
    }

    @Bean
    public Queue topicQueueB() {
        return new Queue(TOPIC_QUEUE_B);
    }

    @Bean
    public Binding topicExchangeBindQueueA(Queue topicQueueA, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueueA).to(topicExchange).with(TOPIC_QUEUE_A);
    }

    @Bean
    public Binding topicExchangeBindTopicQueueALL(Queue topicQueueB, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueueB).to(topicExchange).with("Topic.#");
    }

}
