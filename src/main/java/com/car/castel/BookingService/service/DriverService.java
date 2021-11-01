package com.car.castel.BookingService.service;

import com.car.castel.BookingService.entity.VehicleDriver;

public interface DriverService extends CRUDServices<VehicleDriver>{
    Boolean isEmailTaken(String email);
    VehicleDriver getByEmail(String email);
}
