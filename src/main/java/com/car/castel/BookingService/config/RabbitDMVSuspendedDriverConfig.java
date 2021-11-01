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
public class RabbitDMVSuspendedDriverConfig {

    @Value("${exchange.dmv}")
    private String exchange;

    @Value("${routingKey.dmv}")
    private String routingKey;

    @Value("${queues.dmv}")
    private String queue;

    public static final String QUEUE = "dmv";

    @Bean
    public Queue dmvMessageQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange dmvMessageExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding dmvMessageBinding(
            @Qualifier("dmvMessageQueue") Queue newMessageQueue,
            @Qualifier("dmvMessageExchange") TopicExchange newMessageExchange
    ) {
        return BindingBuilder.bind(newMessageQueue).to(newMessageExchange).with(routingKey);
    }
}
