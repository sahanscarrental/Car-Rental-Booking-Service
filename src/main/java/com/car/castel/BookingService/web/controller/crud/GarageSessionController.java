package com.car.castel.BookingService.web.controller.crud;

import com.car.castel.BookingService.entity.GarageSession;
import com.car.castel.BookingService.service.impl.GarageSessionService;
import com.car.castel.BookingService.web.controller.CRUDController;
import com.car.castel.BookingService.web.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@RequestMapping("/api/garage-session")
@RestController
public class GarageSessionController implements CRUDController<GarageSession> {

    @Autowired
    private GarageSessionService garageSessionService;

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse> create(@RequestBody GarageSession garageSession) throws Exception {
        return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                .message("Created")
                        .body(garageSessionService.create(garageSession))
                        .timestamp(new Date())
                .build());
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @RequestBody GarageSession garageSession) throws Exception {
        return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                .message("Updated")
                        .body(garageSessionService.update(id, garageSession))
                        .timestamp(new Date())
                .build());
    }

    @GetMapping("/{uuid}")
    @Override
    public ResponseEntity<ApiResponse> get(@PathVariable UUID uuid) throws Exception {
        return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                .message(null)
                        .body(garageSessionService.get(uuid))
                        .timestamp(new Date())
                .build());
    }

    @DeleteMapping("/{uuid}")
    @Override
    public ResponseEntity delete(@PathVariable UUID uuid) throws Exception {
        garageSessionService.delete(uuid);
        return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body("")
                .message("Deleted")
                        .timestamp(new Date())
                .build());
    }

    @PostMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> createAll(@RequestBody List<GarageSession> list) throws Exception {
        return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body(garageSessionService.createAll(list))
                .message(null)
                        .timestamp(new Date())
                .build());
    }

    @GetMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> getAll() throws Exception {
        return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body(garageSessionService.getAll())
                .message(null)
                        .timestamp(new Date())
                .build());
    }
}
