package com.car.castel.BookingService.web.controller.crud;

import com.car.castel.BookingService.entity.Garage;
import com.car.castel.BookingService.service.impl.GarageService;
import com.car.castel.BookingService.web.controller.CRUDController;
import com.car.castel.BookingService.web.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/garage")
public class GarageController implements CRUDController<Garage> {

    @Autowired
    private GarageService garageService;


    @PostMapping
    @Override
    public ResponseEntity<ApiResponse> create(@RequestBody Garage garage) {
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body(garageService.create(garage))
                 .message("Created")
                        .timestamp(new Date())
                .build());
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @RequestBody Garage garage) {
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body(garageService.update(id, garage))
                 .message("Updated")
                        .timestamp(new Date())
                .build());
    }

    @GetMapping("/{uuid}")
    @Override
    public ResponseEntity<ApiResponse> get(@PathVariable UUID uuid) {
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body(garageService.get(uuid))
                 .message(null)
                        .timestamp(new Date())
                .build());
    }

    @DeleteMapping
    @Override
    public ResponseEntity delete(@PathVariable UUID uuid) {
        garageService.delete(uuid);
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                 .message("Deleted")
                        .body("")
                        .timestamp(new Date())
                .build());
    }

    @PostMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> createAll(@RequestBody List<Garage> list) {
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                 .message(null)
                        .body(garageService.createAll(list))
                        .timestamp(new Date())
                .build());
    }

    @GetMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> getAll() {
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                 .message(null)
                        .body(garageService.getAll())
                        .timestamp(new Date())
                .build());
    }
}
