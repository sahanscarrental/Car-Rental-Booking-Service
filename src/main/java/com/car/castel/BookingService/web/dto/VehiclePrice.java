package com.car.castel.BookingService.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VehiclePrice {
    private String name;
    private String ratePerMonth;
    private String ratePerWeek;
}
