package com.car.castel.BookingService.event.modal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OTPSendEventModal implements Serializable {

    static final long serialVersionUID = 3181265350610699090L;

    private String to;

    @Override
    public String toString() {
        return "OTPSendEventModal{" +
                "to='" + to + '\'' +
                '}';
    }
}
