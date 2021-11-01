package com.car.castel.BookingService.web.exception;

public class ExceptionWithMessage extends RuntimeException{
    public ExceptionWithMessage(String message) {
        super(message);
    }
}
