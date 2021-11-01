package com.car.castel.BookingService.event.modal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DMVSuspendedDriverEvent {
    private String regNo;
    private Date time;
    private UUID frontImageId;

    @Override
    public String toString() {
        return "DMVSuspendedDriverEvent{" +
                "regNo='" + regNo + '\'' +
                ", time=" + time +
                ", frontImageId=" + frontImageId +
                '}';
    }
}
