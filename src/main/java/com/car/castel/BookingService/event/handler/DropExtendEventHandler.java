package com.car.castel.BookingService.event.handler;

import com.car.castel.BookingService.event.Event;
import com.car.castel.BookingService.event.modal.DropTimeExtendedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;



@Slf4j
public class DropExtendEventHandler implements Event {

    private String exchange;
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final DropTimeExtendedEvent dropTimeExtendedEvent;

    public DropExtendEventHandler(DropTimeExtendedEvent event, RabbitTemplate template, String routingKey, String exchange) {
        this.rabbitTemplate = template;
        this.dropTimeExtendedEvent = event;
        this.routingKey = routingKey;
        this.exchange = exchange;
    }

    @Override
    public void execute() {
        log.info("executing the DropExtendEventHandler with " + dropTimeExtendedEvent.toString());
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey,dropTimeExtendedEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
