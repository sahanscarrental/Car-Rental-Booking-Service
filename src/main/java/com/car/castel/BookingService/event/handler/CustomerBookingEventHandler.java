package com.car.castel.BookingService.event.handler;

import com.car.castel.BookingService.event.Event;
import com.car.castel.BookingService.event.modal.CustomerBookingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class CustomerBookingEventHandler implements Event {

    private String exchange;
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final CustomerBookingEvent customerBookingEvent;

    public CustomerBookingEventHandler( CustomerBookingEvent customerBookingEvent,RabbitTemplate rabbitTemplate,String routingKey, String exchange ) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.rabbitTemplate = rabbitTemplate;
        this.customerBookingEvent = customerBookingEvent;
    }

    @Override
    public void execute() {
        log.info("executing the DMVSuspendedDriverEventHandler with " + customerBookingEvent.toString());
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, customerBookingEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
