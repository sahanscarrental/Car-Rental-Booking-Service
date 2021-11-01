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
public class RabbitEmailVeryFyConfig {

    @Value("${exchange.email-verify}")
    private String exchange;

    @Value("${routingKey.email-verify}")
    private String routingKey;

    @Value("${queues.email-verify}")
    private String queue;

    public static final String QUEUE = "email_verify";

    @Bean
    public Queue emailVerifyMessageQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange emailVerifyMessageExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding emailVerifyMessageBinding(
            @Qualifier("emailVerifyMessageQueue") Queue newMessageQueue,
            @Qualifier("emailVerifyMessageExchange") TopicExchange newMessageExchange
    ) {
        return BindingBuilder.bind(newMessageQueue).to(newMessageExchange).with(routingKey);
    }
}
