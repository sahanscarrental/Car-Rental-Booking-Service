package com.car.castel.BookingService.web.controller.crud;

import com.car.castel.BookingService.entity.PaymentTransaction;
import com.car.castel.BookingService.service.impl.PaymentTransactionService;
import com.car.castel.BookingService.web.controller.CRUDController;
import com.car.castel.BookingService.web.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentTransactionController implements CRUDController<PaymentTransaction> {

    @Autowired
    private PaymentTransactionService paymentTransactionService;

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse> create(@RequestBody PaymentTransaction paymentTransaction) {
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body(paymentTransactionService.create(paymentTransaction))
                        .timestamp(new Date())
                .build());
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @RequestBody  PaymentTransaction paymentTransaction) {
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body(paymentTransactionService.update(id, paymentTransaction))
                        .timestamp(new Date())
                .build());
    }

    @GetMapping("/{uuid}")
    @Override
    public ResponseEntity<ApiResponse> get(@PathVariable UUID uuid) {
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body(paymentTransactionService.get(uuid))
                        .timestamp(new Date())
                .build());
    }


    @DeleteMapping("/{uuid}")
    @Override
    public ResponseEntity delete(@PathVariable UUID uuid) {
        paymentTransactionService.delete(uuid);
        return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body("")
                        .timestamp(new Date())
                .build());
    }

    @PostMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> createAll(@RequestBody List<PaymentTransaction> list) {
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body(paymentTransactionService.createAll(list))
                        .timestamp(new Date())
                .build());
    }

    @GetMapping("/all")
    @Override
    public ResponseEntity<ApiResponse> getAll() {
         return ResponseEntity.ok(ApiResponse.builder()
                        .status(true)
                        .body(paymentTransactionService.getAll())
                        .timestamp(new Date())
                .build());
    }
}
