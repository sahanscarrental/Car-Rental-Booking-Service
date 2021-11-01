package com.car.castel.BookingService.web.controller;

import com.car.castel.BookingService.service.BandedDriver;
import com.car.castel.BookingService.web.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

@RestController
@RequestMapping("/api/dmv")
public class DMVController {


    @Autowired
    private BandedDriver bandedDriver;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestParam("file") MultipartFile file) throws Exception{

        BufferedReader br;
        bandedDriver.initialize();
        try {
            String line;
            InputStream is = file.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                String[] strings = line.split(",");
                String ln = strings[1];
                String val = strings[2]+"-"+strings[3]+strings[4];
                bandedDriver.add(ln, val);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return ResponseEntity.ok(ApiResponse
                .builder()
                .status(true)
                .timestamp(new Date())
                .message("updated ")
                .body("")
                .build());
    }
}
