package com.car.castel.BookingService.web.controller.crud;

import com.car.castel.BookingService.entity.BookingRecord;
import com.car.castel.BookingService.service.BookingService;
import com.car.castel.BookingService.web.controller.CRUDController;
import com.car.castel.BookingService.web.dto.request.BookingRequest;
import com.car.castel.BookingService.web.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/booking")
public class BookingRecordController implements CRUDController<BookingRecord> {

    @Autowired
    private BookingService bookingRecordService;


    @GetMapping("/report")
    public ResponseEntity<ApiResponse> getReport(
            @QueryParam(value = "from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @QueryParam(value = "to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to
    ) {

        return ResponseEntity.ok(ApiResponse
                .builder()
                .timestamp(new Date())
                .body(bookingRecordService.getByDateRange(from, to))
                .message(null)
                .status(true)
                .build());
    }


    @GetMapping("/cost")
    public ResponseEntity<ApiResponse> getCost(
            @QueryParam(value = "from") Date from,
            @QueryParam(value = "to") Date to,
            @QueryParam(value = "to") Double costPerDay
    ) {
        return ResponseEntity.ok(ApiResponse
                .builder()
                .timestamp(new Date())
                .message(null)
                .body(bookingRecordService.calculateCost(from, to,costPerDay))
                .status(true)
                .build());
    }

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse> create(@RequestBody BookingRecord bookingRecord) {

        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .message("Created")
                        .body(bookingRecordService.create(bookingRecord))
                        .status(true)
                .build());
    }

    @PostMapping("/createFromDTO")
    public ResponseEntity<ApiResponse> createFromDTO(@RequestBody BookingRequest bookingRequest) {

        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .message("Created")
                        .body(bookingRecordService.createFromDTO(bookingRequest))
                        .status(true)
                .build());
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @RequestBody BookingRecord bookingRecord) {
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .message("Updated")
                        .body(bookingRecordService.update(id, bookingRecord))
                        .status(true)
                .build());
    }

    @PutMapping("/extend-drop")
    public ResponseEntity<ApiResponse> updateExtendDrop(@RequestParam UUID id,@RequestParam String extendedDropDateTime) {
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .message("Updated")
                        .body(bookingRecordService.extendDropTime(id, extendedDropDateTime))
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
                        .body(bookingRecordService.get(uuid))
                        .status(true)
                .build());
    }

    @DeleteMapping("/{uuid}")
    @Override
    public ResponseEntity delete(@PathVariable UUID uuid) {
        bookingRecordService.delete(uuid);
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
    public ResponseEntity<ApiResponse> createAll(@RequestBody List<BookingRecord> list) {
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .body(bookingRecordService.createAll(list))
                        .message(null)
                        .status(true)
                .build());
    }

    @GetMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(ApiResponse
                .builder()
                        .timestamp(new Date())
                        .body(bookingRecordService.getAll())
                        .message(null)
                        .status(true)
                .build());
    }

    @GetMapping("/all/{driver}")
    public ResponseEntity<ApiResponse> getAllByDriver(@PathVariable(value = "driver") UUID driver) {
        return ResponseEntity.ok(ApiResponse
                .builder()
                .timestamp(new Date())
                .body(bookingRecordService.getAll(driver))
                .status(true)
                .message(null)
                .build());
    }
}
