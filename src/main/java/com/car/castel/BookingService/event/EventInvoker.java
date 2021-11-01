package com.car.castel.BookingService.event;

import java.util.ArrayList;
import java.util.List;


/**
 * This class keep list of event and invoke all
 */
public class EventInvoker {

    /**
     * Store the list of Event
     */
    private List<Event> events;

    public EventInvoker() {
        this.events = new ArrayList<>();;
    }

    public Integer getSize(){
        return this.events.size();
    }

    public void addEvent(Event iPostEvent){
        events.add(iPostEvent);
    }

    public void invoke(){
        events.forEach(Event::execute);
    }
}
