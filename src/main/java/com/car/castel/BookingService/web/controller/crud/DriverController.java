package com.car.castel.BookingService.web.controller.crud;

import com.car.castel.BookingService.entity.VehicleDriver;
import com.car.castel.BookingService.entity.type.DriverState;
import com.car.castel.BookingService.service.DriverService;
import com.car.castel.BookingService.web.controller.CRUDController;
import com.car.castel.BookingService.web.exception.ExceptionWithMessage;
import com.car.castel.BookingService.web.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/driver")
public class DriverController implements CRUDController<VehicleDriver> {

    @Autowired
    private DriverService driverService;

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse> create(@RequestBody VehicleDriver vehicleDriver) {
        Boolean emailTaken = driverService.isEmailTaken(vehicleDriver.getEmail());
        if (emailTaken) throw new ExceptionWithMessage("Email already taken");

        vehicleDriver.setSuccessBookings(0L);
        vehicleDriver.setEmailVerified(false);
        vehicleDriver.setPhoneNoVerified(false);
        vehicleDriver.setDriverState(DriverState.ADMIN_DECISION_PENDING);
        VehicleDriver savedVehicleDriver = driverService.create(vehicleDriver);
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .body(savedVehicleDriver)
                .message("Created")
                        .status(true)
                .build());
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @RequestBody  VehicleDriver vehicleDriver) {
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                .message("Updated")
                        .body(driverService.update(id, vehicleDriver))
                        .status(true)
                .build());
    }

    @GetMapping("/{uuid}")
    @Override
    public ResponseEntity<ApiResponse> get(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .body(driverService.get(uuid))
                .message(null)
                        .status(true)
                .build());
    }

    @DeleteMapping("/{uuid}")
    @Override
    public ResponseEntity delete(@PathVariable UUID uuid) {
        driverService.delete(uuid);
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .body("")
                .message("Deleted")
                        .status(true)
                .build());
    }

    @PostMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> createAll(@RequestBody List<VehicleDriver> list) {
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .body(driverService.createAll(list))
                .message(null)
                        .status(true)
                .build());
    }

    @GetMapping("/all-black-listed")
    public ResponseEntity<ApiResponse> getAllBlackListed() {
        List<VehicleDriver> vehicleDrivers = driverService.getAll();
        List<VehicleDriver> vehicleDrivers1 = vehicleDrivers.stream().filter(vehicleDriver -> vehicleDriver.getDriverState() == DriverState.BLACK_LISTED).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse
                .builder()
                .timestamp(new Date())
                .body(vehicleDrivers1)
                .status(true)
                .message(null)
                .build());
    }

    @GetMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .body(driverService.getAll())
                        .status(true)
                .message(null)
                .build());
    }
}
