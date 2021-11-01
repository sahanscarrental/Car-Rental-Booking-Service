package com.car.castel.BookingService.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FoughtPassword {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @Override
    public String toString() {
        return "FoughtPassword{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
