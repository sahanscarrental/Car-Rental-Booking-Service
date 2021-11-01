package com.car.castel.BookingService.service.impl;

import com.car.castel.BookingService.entity.BookingRecord;
import com.car.castel.BookingService.entity.Vehicle;
import com.car.castel.BookingService.repository.BookingRecordRepository;
import com.car.castel.BookingService.repository.VehicleRepository;
import com.car.castel.BookingService.service.CRUDServices;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import com.car.castel.BookingService.web.exception.ExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleService implements CRUDServices<Vehicle> {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRecordRepository bookingRecordRepository;

    @Override
    public Vehicle create(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle update(UUID uuid, Vehicle vehicle) {
        Optional<Vehicle> vehicle1 = vehicleRepository.findById(uuid);
        if (vehicle1.isPresent()){
            return vehicleRepository.save(vehicle);
        }else {
            throw new EntityNotFoundException(Vehicle.class,"id",uuid.toString());
        }
    }

    @Override
    public Vehicle get(UUID uuid) {
        Optional<Vehicle> vehicle1 = vehicleRepository.findById(uuid);
        if (vehicle1.isPresent()){
            return vehicle1.get();
        }else {
            throw new EntityNotFoundException(Vehicle.class,"id",uuid.toString());
        }
    }

    @Override
    public void delete(UUID uuid) {
        Vehicle vehicle = vehicleRepository.findById(uuid).orElse(null);
        if (vehicle != null) {
            // check whether the vehicle has any trips
            List<BookingRecord> allByVehicleAndBookingRecordState = bookingRecordRepository.findAnyBookingsForVehicle(vehicle);
            if (allByVehicleAndBookingRecordState != null && allByVehicleAndBookingRecordState.size() > 0) {
                throw new ExceptionWithMessage("Vehicle trying to delete has Bookings Records");
            }
            vehicleRepository.delete(vehicle);
        } else {
            throw new EntityNotFoundException(Vehicle.class, "id", uuid.toString());
        }
    }

    @Override
    public List<Vehicle> createAll(List<Vehicle> list) {
        return list.stream().parallel().map(this::create).collect(Collectors.toList());
    }

    @Override
    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }
}
