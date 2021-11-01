package com.car.castel.BookingService.event.modal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EmailVerifiedEvent {
    private String to;

    @Override
    public String toString() {
        return "OTPSendEvent{" +
                "to='" + to + '\'' +
                '}';
    }
}
