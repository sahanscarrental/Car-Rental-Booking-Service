package com.car.castel.BookingService.web.exception;

public class InvalidCredentialException extends RuntimeException{
    public InvalidCredentialException() {
        super("Invalid password");
    }
}
