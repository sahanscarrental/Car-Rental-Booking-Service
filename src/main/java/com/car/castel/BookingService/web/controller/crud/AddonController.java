package com.car.castel.BookingService.web.controller.crud;

import com.car.castel.BookingService.entity.Addon;
import com.car.castel.BookingService.service.impl.AddonService;
import com.car.castel.BookingService.web.controller.CRUDController;
import com.car.castel.BookingService.web.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addon")
public class AddonController implements CRUDController<Addon> {

    @Autowired
    private AddonService addonService;

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse> create(@RequestBody Addon addon) throws Exception{
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .status(true)
                        .timestamp(new Date())
                        .body(addonService.create(addon))
                .build());
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id,@RequestBody Addon addon)  throws Exception {
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .message("updated")
                        .body(addonService.update(id, addon))
                        .status(true)
                .build());
    }

    @GetMapping("/{uuid}")
    @Override
    public ResponseEntity<ApiResponse> get(@PathVariable  UUID uuid)  throws Exception{

        return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .message(null)
                        .body(addonService.get(uuid))
                        .timestamp(new Date())
                .build());
    }

    @DeleteMapping("/{uuid}")
    @Override
    public ResponseEntity delete(UUID uuid)  throws Exception{
        addonService.delete(uuid);
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
    public ResponseEntity<ApiResponse> createAll(@RequestBody List<Addon> list)  throws Exception {
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .status(true)
                        .message(null)
                        .body(addonService.createAll(list))
                        .timestamp(new Date())
                .build());
    }

    @GetMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> getAll()  throws Exception{
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .status(true)
                        .message(null)
                        .body(addonService.getAll())
                .build());
    }

    @GetMapping("/allAvailable")
    public ResponseEntity<ApiResponse> getAllAvailable()  throws Exception{
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .status(true)
                        .message(null)
                        .body(addonService.getAllAvailable())
                .build());
    }
}
