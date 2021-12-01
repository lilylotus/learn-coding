package cn.nihility.rabbitmq.producer.direct;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * direct exchange (直连型交换机)
 */
@Configuration(proxyBeanMethods = false)
public class DirectConfiguration {

    public static final String NORMAL_DIRECT_EXCHANGE = "NormalDirectExchange";
    public static final String NORMAL_DIRECT_QUEUE = "NormalDirectQueue";
    public static final String NORMAL_DIRECT_JSON_QUEUE = "NormalDirectJsonQueue";
    public static final String NORMAL_DIRECT_EXCHANGE_BIND_QUEUE_KEY = "NormalDirectExchangeBindQueueKey";
    public static final String NORMAL_DIRECT_EXCHANGE_BIND_JSON_QUEUE_KEY = "NormalDirectExchangeBindJsonQueueKey";

    /* ---------- 普通队列/交换机 ---------- */
    @Bean
    public DirectExchange normalDirectExchange() {
        // 默认是 durable - true, autoDelete - false
        return new DirectExchange(NORMAL_DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public Queue normalDirectQueue() {
        // 默认 durable - true, exclusive - false, autoDelete - false
        return new Queue(NORMAL_DIRECT_QUEUE, true, false, false);
    }

    @Bean
    public Queue normalDirectJsonQueue() {
        // 默认 durable - true, exclusive - false, autoDelete - false
        return new Queue(NORMAL_DIRECT_JSON_QUEUE);
    }

    /* broker 交换机依据不同的 key 把消息分发的不同的 queue */
    @Bean
    public Binding normalDirectExchangeQueueBinding(DirectExchange normalDirectExchange, Queue normalDirectQueue) {
        /* broker 的 exchange 和 queue 通过 key 绑定起来 */
        return BindingBuilder.bind(normalDirectQueue)
            .to(normalDirectExchange)
            .with(NORMAL_DIRECT_EXCHANGE_BIND_QUEUE_KEY);
    }

    @Bean
    public Binding normalDirectExchangeJsonQueueBinding(DirectExchange normalDirectExchange, Queue normalDirectJsonQueue) {
        /* broker 的 exchange 和 queue 通过 key 绑定起来 */
        return BindingBuilder.bind(normalDirectJsonQueue)
            .to(normalDirectExchange)
            .with(NORMAL_DIRECT_EXCHANGE_BIND_JSON_QUEUE_KEY);
    }

    /* ---------- 死信队列/交换机 ---------- */
    public static final String DIRECT_DEAD_LETTER_EXCHANGE = "DirectDeadLetterExchange";
    public static final String DIRECT_DEAD_LETTER_QUEUE = "DirectDeadLetterQueue";
    public static final String DIRECT_DEAD_LETTER_EXCHANGE_BIND_DEAD_QUEUE_KEY = "DirectDeadLetterExchangeBindDeadQueueKey";

    @Bean
    public DirectExchange directDeadLetterExchange() {
        /* 定义死信交换机 */
        return new DirectExchange(DIRECT_DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Queue directDeadLetterQueue() {
        /* durable:是否持久化,默认是 false, 持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
           exclusive: 默认也是 false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于 durable
           autoDelete: 是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
         */
        return new Queue(DIRECT_DEAD_LETTER_QUEUE);
    }

    @Bean
    public Binding directDeadLetterExchangeBindQueue(DirectExchange directDeadLetterExchange, Queue directDeadLetterQueue) {
        // 将队列和交换机绑定, 并设置用于匹配键：deadLetterQueueRoutingKey
        return BindingBuilder.bind(directDeadLetterQueue)
            .to(directDeadLetterExchange)
            .with(DIRECT_DEAD_LETTER_EXCHANGE_BIND_DEAD_QUEUE_KEY);
    }

    /* ---------- 定义业务队列绑定到死信队列/交换机 ---------- */
    public static final String DIRECT_BUSINESS_QUEUE_WITH_DEAD_LETTER = "DirectBusinessQueue";
    public static final String DIRECT_EXCHANGE_BIND_DIRECT_BUSINESS_QUEUE_KEY = "DirectExchangeBindBusinessQueueKey";

    @Bean
    public Queue directBusinessQueue() {
        /* 定义业务队列 */
        Map<String, Object> args = new HashMap<>(2);
        // x-dead-letter-exchange 这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DIRECT_DEAD_LETTER_EXCHANGE);
        // x-dead-letter-routing-key 这里声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", DIRECT_DEAD_LETTER_EXCHANGE_BIND_DEAD_QUEUE_KEY);
        return new Queue(DIRECT_BUSINESS_QUEUE_WITH_DEAD_LETTER, true, false, false, args);
    }

    @Bean
    public Binding directExchangeBindBusinessQueue(DirectExchange normalDirectExchange, Queue directBusinessQueue) {
        // 将队列和交换机绑定, 并设置用于匹配键
        return BindingBuilder.bind(directBusinessQueue)
            .to(normalDirectExchange)
            .with(DIRECT_EXCHANGE_BIND_DIRECT_BUSINESS_QUEUE_KEY);
    }

    /* ---------- 定义独立的交换机 ---------- */
    public static final String DIRECT_LONELY_EXCHANGE = "DirectLonelyExchange";

    @Bean
    public DirectExchange directLonelyExchange() {
        return new DirectExchange(DIRECT_LONELY_EXCHANGE);
    }

}
