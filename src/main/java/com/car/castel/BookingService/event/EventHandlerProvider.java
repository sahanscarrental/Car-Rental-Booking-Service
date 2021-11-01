package com.car.castel.BookingService.event;

import com.car.castel.BookingService.event.handler.CustomerBookingEventHandler;
import com.car.castel.BookingService.event.handler.DMVSuspendedDriverEventHandler;
import com.car.castel.BookingService.event.handler.DropExtendEventHandler;
import com.car.castel.BookingService.event.handler.OTPSendEventHandler;
import com.car.castel.BookingService.event.modal.CustomerBookingEvent;
import com.car.castel.BookingService.event.modal.DMVSuspendedDriverEvent;
import com.car.castel.BookingService.event.modal.DropTimeExtendedEvent;
import com.car.castel.BookingService.event.modal.OTPSendEventModal;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This class provide relevant EventHandler for the given event
 */
@Service
public class EventHandlerProvider {
    @Autowired
    private RabbitTemplate template;

    public Event getBroker(EventType eventType, Object event){
        switch (eventType){
            case DMV_SUSPENDED:
                return new DMVSuspendedDriverEventHandler((DMVSuspendedDriverEvent) event, template, dmvRoutingKey, dmvExchange);
            case DROP_TIME_EXTEND:
                return new DropExtendEventHandler((DropTimeExtendedEvent) event, template, dropExtendRoutingKey, dropExtendExchange);
            case OTP_SEND:
                return new OTPSendEventHandler((OTPSendEventModal) event, template, otpSendRoutingKey, otpSendExchange);
            case BOOKING_CREATED:
            case BOOKING_CANCELLED:
            case VEHICLE_PICKED_UP:
            case VEHICLE_DROP_OFF:
                return new CustomerBookingEventHandler((CustomerBookingEvent) event, template, bookingRoutingKey, bookingExchange);
            default:
                return new CustomerBookingEventHandler((CustomerBookingEvent) event, template, bookingRoutingKey, bookingExchange);
        }
    }

    @Value("${exchange.otp-send}")
    private String otpSendExchange;

    @Value("${routingKey.otp-send}")
    private String otpSendRoutingKey;

    @Value("${exchange.drop-extend}")
    private String dropExtendExchange;

    @Value("${routingKey.drop-extend}")
    private String dropExtendRoutingKey;

    @Value("${exchange.dmv}")
    private String dmvExchange;

    @Value("${routingKey.dmv}")
    private String dmvRoutingKey;

    @Value("${routingKey.booking}")
    private String bookingRoutingKey;

    @Value("${exchange.booking}")
    private String bookingExchange;
}
