package com.car.castel.BookingService.event.modal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DropTimeExtendedEvent {
    private String driverEmail;
    private String driverPhoneNo;
    private String driverName;
    private String vehicleNo;
    private Date extendedTo;

    @Override
    public String toString() {
        return "DropTimeExtendedEvent{" +
                "driverEmail='" + driverEmail + '\'' +
                ", driverPhoneNo='" + driverPhoneNo + '\'' +
                ", driverName='" + driverName + '\'' +
                ", vehicleNo='" + vehicleNo + '\'' +
                ", extendedTo=" + extendedTo +
                '}';
    }
}
