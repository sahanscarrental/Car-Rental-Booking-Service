package com.car.castel.BookingService.web.dto.request;

import com.car.castel.BookingService.entity.Addon;
import com.car.castel.BookingService.entity.Vehicle;
import com.car.castel.BookingService.entity.VehicleDriver;
import com.car.castel.BookingService.entity.type.BookingRecordState;
import com.car.castel.BookingService.entity.type.Currency;
import com.car.castel.BookingService.entity.type.PaymentState;
import com.car.castel.BookingService.entity.type.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingRequest {

    private UUID id;

    private Vehicle vehicle;

    private VehicleDriver vehicleDriver;

    private String pickUpTime;

    private String dropTime;

    private Date extendedDropTime;

    private PaymentType paymentType;

    private List<Addon> addons = new ArrayList<>();

    private Boolean returnAtNight;
}
