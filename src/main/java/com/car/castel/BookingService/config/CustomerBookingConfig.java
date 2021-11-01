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
public class CustomerBookingConfig {

    @Value("${exchange.booking}")
    private String exchange;

    @Value("${routingKey.booking}")
    private String routingKey;

    @Value("${queues.booking}")
    private String queue;

    public static final String QUEUE = "booking";

    @Bean
    public Queue bookingMessageQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange bookingMessageExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding bookingMessageBinding(
            @Qualifier("bookingMessageQueue") Queue newMessageQueue,
            @Qualifier("bookingMessageExchange") TopicExchange newMessageExchange
    ) {
        return BindingBuilder.bind(newMessageQueue).to(newMessageExchange).with(routingKey);
    }
}
