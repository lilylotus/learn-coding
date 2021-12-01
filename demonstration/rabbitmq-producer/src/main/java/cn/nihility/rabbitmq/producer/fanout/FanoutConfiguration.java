package cn.nihility.rabbitmq.producer.fanout;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class FanoutConfiguration {

    public static final String FANOUT_EXCHANGE = "NormalFanoutExchange";
    public static final String FANOUT_QUEUE_A = "NormalFanoutQueue.A";
    public static final String FANOUT_QUEUE_B = "NormalFanoutQueue.B";
    public static final String FANOUT_QUEUE_C = "NormalFanoutQueue.C";

    @Bean
    public FanoutExchange normalFanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Queue fanoutQueueA() {
        return new Queue(FANOUT_QUEUE_A);
    }

    @Bean
    public Queue fanoutQueueB() {
        return new Queue(FANOUT_QUEUE_B);
    }

    @Bean
    public Queue fanoutQueueC() {
        return new Queue(FANOUT_QUEUE_C);
    }

    @Bean
    public Binding normalFanoutExchangeBindQueueA(Queue fanoutQueueA, FanoutExchange normalFanoutExchange) {
        return BindingBuilder.bind(fanoutQueueA).to(normalFanoutExchange);
    }

    @Bean
    public Binding normalFanoutExchangeBindQueueB(Queue fanoutQueueB, FanoutExchange normalFanoutExchange) {
        return BindingBuilder.bind(fanoutQueueB).to(normalFanoutExchange);
    }

    @Bean
    public Binding normalFanoutExchangeBindQueueC(Queue fanoutQueueC, FanoutExchange normalFanoutExchange) {
        return BindingBuilder.bind(fanoutQueueC).to(normalFanoutExchange);
    }

}
