package cn.nihility.rabbitmq.producer.delay;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * rabbitmq 延迟队列配置
 */
@Configuration
public class DelayedConfiguration {

    public static final String DELAYED_QUEUE = "DelayedQueue";
    public static final String DELAYED_EXCHANGE = "DelayedExchange";
    public static final String DELAYED_ROUTE_KEY = "DelayedRouteKey";

    @Bean
    public Queue delayedQueue() {
        return new Queue(DELAYED_QUEUE);
    }

    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>(4);
        // 延迟类型可以选已有的: direct, topic
        args.put("x-delayed-type", "direct");
        // 类型必须是 x-delayed-message
        return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean
    public Binding delayedBinding() {
        return BindingBuilder.bind(delayedQueue())
            .to(delayedExchange()).with(DELAYED_ROUTE_KEY).noargs();
    }

}
