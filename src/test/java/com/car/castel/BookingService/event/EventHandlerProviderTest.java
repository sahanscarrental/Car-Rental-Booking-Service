package com.car.castel.BookingService.event;

import com.car.castel.BookingService.BookingServiceApplication;
import com.car.castel.BookingService.event.handler.DMVSuspendedDriverEventHandler;
import com.car.castel.BookingService.event.handler.DropExtendEventHandler;
import com.car.castel.BookingService.event.handler.OTPSendEventHandler;
import com.car.castel.BookingService.event.modal.DMVSuspendedDriverEvent;
import com.car.castel.BookingService.event.modal.DropTimeExtendedEvent;
import com.car.castel.BookingService.event.modal.OTPSendEventModal;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = BookingServiceApplication.class)
public class EventHandlerProviderTest {

    @InjectMocks
    private EventHandlerProvider eventHandlerProvider = new EventHandlerProvider();


    @Test
    public void getBroker() {
        Event broker = eventHandlerProvider.getBroker(EventType.DMV_SUSPENDED, DMVSuspendedDriverEvent.builder().build());
        assertEquals(broker.getClass(), DMVSuspendedDriverEventHandler.class );

        Event broker1 = eventHandlerProvider.getBroker(EventType.DROP_TIME_EXTEND, new DropTimeExtendedEvent());
        assertEquals(broker1.getClass(), DropExtendEventHandler.class );

        Event broker2 = eventHandlerProvider.getBroker(EventType.OTP_SEND, new OTPSendEventModal());
        assertEquals(broker2.getClass(), OTPSendEventHandler.class );
    }
}