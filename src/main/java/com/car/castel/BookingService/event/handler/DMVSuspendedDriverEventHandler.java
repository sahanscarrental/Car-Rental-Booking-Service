package com.car.castel.BookingService.event.handler;

import com.car.castel.BookingService.event.Event;
import com.car.castel.BookingService.event.modal.DMVSuspendedDriverEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class DMVSuspendedDriverEventHandler implements Event {

    private String exchange;
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final DMVSuspendedDriverEvent dmvSuspendedDriverEvent;

    public DMVSuspendedDriverEventHandler( DMVSuspendedDriverEvent dmvSuspendedDriverEvent,RabbitTemplate rabbitTemplate,String routingKey, String exchange ) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.rabbitTemplate = rabbitTemplate;
        this.dmvSuspendedDriverEvent = dmvSuspendedDriverEvent;
    }

    @Override
    public void execute() {
        log.info("executing the DMVSuspendedDriverEventHandler with " + dmvSuspendedDriverEvent.toString());
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey,dmvSuspendedDriverEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
