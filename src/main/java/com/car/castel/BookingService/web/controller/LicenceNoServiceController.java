package com.car.castel.BookingService.web.controller;

import com.car.castel.BookingService.service.FakeClaim;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fake-claim")
public class LicenceNoServiceController {

    @Autowired
    private FakeClaim fakeClaim;


    @PostMapping
    public ResponseEntity<Object> sync (@RequestBody List<String> liNos) {

        fakeClaim.initialize();
        liNos.stream().parallel().forEach(s -> fakeClaim.add(s, s));
        return ResponseEntity.ok(200);
    }
}
