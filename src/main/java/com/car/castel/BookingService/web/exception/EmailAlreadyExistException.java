package com.car.castel.BookingService.web.exception;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException() {
        super("Email is already exists");
    }
}
