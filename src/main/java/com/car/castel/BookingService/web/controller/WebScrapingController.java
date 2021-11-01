package com.car.castel.BookingService.web.controller;

import com.car.castel.BookingService.web.dto.VehiclePrice;
import com.car.castel.BookingService.web.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/web-scraping")
public class WebScrapingController {

    @GetMapping("/newbedev")
    public ResponseEntity<ApiResponse> getVehiclePrices(){

        List<VehiclePrice> vehiclePriceList = new ArrayList<>();
        try {

            Document doc = Jsoup.connect("https://www.malkey.lk/rates/self-drive-rates.html#standard").get();
            for (Element table : doc.select("table")) {
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    if (tds.size() == 4) {
                        String vehicle = tds.get(0).text();
                        String ratePerMonth = tds.get(1).text();
                        String ratePerWeek = tds.get(2).text();

                        VehiclePrice vehiclePrice = VehiclePrice
                                .builder()
                                .name(vehicle)
                                .ratePerMonth(ratePerMonth)
                                .ratePerWeek(ratePerWeek)
                                .build();
                        vehiclePriceList.add(vehiclePrice);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(
                ApiResponse
                        .builder()
                        .status(true)
                        .message("Done")
                        .body(vehiclePriceList)
                        .build()
        );
    }
}
