package com.car.castel.BookingService.event;

import com.car.castel.BookingService.BookingServiceApplication;
import com.car.castel.BookingService.event.modal.DMVSuspendedDriverEvent;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;


@SpringBootTest(classes = BookingServiceApplication.class)
public class EventInvokerTest {

    @InjectMocks
    private EventHandlerProvider eventHandlerProvider = new EventHandlerProvider();



    @Test
    public void addEvent() {
        Event event = eventHandlerProvider.getBroker(EventType.DMV_SUSPENDED, DMVSuspendedDriverEvent.builder().build());
        EventInvoker  eventInvoker = new EventInvoker();
        eventInvoker.addEvent(event);
        assertEquals(Integer.valueOf(1), eventInvoker.getSize());
    }
}