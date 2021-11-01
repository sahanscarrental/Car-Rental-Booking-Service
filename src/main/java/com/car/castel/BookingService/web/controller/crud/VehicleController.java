package com.car.castel.BookingService.web.controller.crud;

import com.car.castel.BookingService.entity.Vehicle;
import com.car.castel.BookingService.service.impl.VehicleService;
import com.car.castel.BookingService.web.controller.CRUDController;
import com.car.castel.BookingService.web.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController implements CRUDController<Vehicle> {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse> create(@RequestBody Vehicle vehicle) {
       return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
               .message("Creted")
                        .body(vehicleService.create(vehicle))
                        .status(true)
                .build());
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @RequestBody Vehicle vehicle) {
       return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
               .message("Updated")
                        .body(vehicleService.update(id, vehicle))
                        .status(true)
                .build());
    }

    @GetMapping("/{uuid}")
    @Override
    public ResponseEntity<ApiResponse> get(@PathVariable UUID uuid) {
       return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
               .message(null)
                        .body(vehicleService.get(uuid))
                        .status(true)
                .build());
    }

    @DeleteMapping("/{uuid}")
    @Override
    public ResponseEntity delete(@PathVariable UUID uuid) {
        vehicleService.delete(uuid);
       return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
               .message("Deleted")
                        .body("")
                        .status(true)
                .build());
    }

    @PostMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> createAll(@RequestBody List<Vehicle> list) {
       return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
               .message(null)
                        .body(vehicleService.createAll(list))
                        .status(true)
                .build());
    }

    @GetMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> getAll() {
       return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
               .message(null)
                        .body(vehicleService.getAll())
                        .status(true)
                .build());
    }
}
