package com.car.castel.BookingService.web.dto.response;

import com.car.castel.BookingService.entity.VehicleDriver;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String id;
	private String username;
	private List<String> roles;
	private VehicleDriver driver;

	public JwtResponse(String token, String id, String username, List<String> roles, VehicleDriver driver) {
		this.token = token;
		this.id = id;
		this.username = username;
		this.roles = roles;
		this.driver = driver;
	}
}
