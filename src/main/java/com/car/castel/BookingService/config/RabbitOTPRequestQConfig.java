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
public class RabbitOTPRequestQConfig {
    @Value("${queues.otp-send}")
    private String queue;

    @Value("${exchange.otp-send}")
    private String exchange;

    @Value("${routingKey.otp-send}")
    private String routingKey;

    @Bean
    public Queue newMessageQueue() {
        return new Queue(queue);
    }

    @Bean
    public TopicExchange newMessageExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding newMessageBinding(
            @Qualifier("newMessageQueue") Queue newMessageQueue,
            @Qualifier("newMessageExchange") TopicExchange newMessageExchange
    ) {
        return BindingBuilder.bind(newMessageQueue).to(newMessageExchange).with(routingKey);
    }
}
