package com.car.castel.BookingService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitDropTimeExtendedConfig {
    @Value("${exchange.drop-extend}")
    private String exchange;

    @Value("${routingKey.drop-extend}")
    private String routingKey;

    @Value("${queues.drop-extend}")
    private String queue;

    public static final String QUEUE = "drop_extend";

    @Bean
    public Queue dropExtendMessageQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange dropExtendMessageExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding dropExtendMessageBinding(
            @Qualifier("dropExtendMessageQueue") Queue newMessageQueue,
            @Qualifier("dropExtendMessageExchange") TopicExchange newMessageExchange
    ) {
        return BindingBuilder.bind(newMessageQueue).to(newMessageExchange).with(routingKey);
    }
}
