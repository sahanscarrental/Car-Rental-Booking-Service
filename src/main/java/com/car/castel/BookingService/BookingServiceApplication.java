package com.car.castel.BookingService;

import com.car.castel.BookingService.service.auth.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import com.car.castel.BookingService.repository.auth.UserRepository;
import com.car.castel.BookingService.entity.auth.User;
import com.car.castel.BookingService.entity.auth.UserStatus;

@SpringBootApplication
@EnableEurekaClient
public class BookingServiceApplication  {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(BookingServiceApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	InitializingBean sendDatabase() {
		return () -> {
			System.out.println("inserting admin user");
			/*INSERT INTO role (id, name) VALUES
					(UNHEX(REPLACE(UUID(),'-','')), 1),
					(UNHEX(REPLACE(UUID(),'-','')), 2);*/
			// create admin user if not available
			// first check if admin user is available
			this.userService.createAdminUser();
		};
	}
}
