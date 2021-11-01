package com.car.castel.BookingService.event.handler;

import com.car.castel.BookingService.event.Event;
import com.car.castel.BookingService.event.modal.OTPSendEventModal;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


@Slf4j
public class OTPSendEventHandler implements Event {

    private String exchange;
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final OTPSendEventModal otpSendEvent;

    public OTPSendEventHandler(OTPSendEventModal event, RabbitTemplate template, String routingKey, String exchange) {
        this.rabbitTemplate = template;
        this.otpSendEvent = event;
        this.routingKey = routingKey;
        this.exchange = exchange;
    }

    @Override
    public void execute() {
        log.info("executing the OTPSendEventBroker to " + otpSendEvent.toString());
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey,otpSendEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
