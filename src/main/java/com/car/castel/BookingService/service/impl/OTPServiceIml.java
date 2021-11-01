package com.car.castel.BookingService.service.impl;

import com.car.castel.BookingService.service.OTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class OTPServiceIml implements OTPService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OTPServiceIml.class);


    public static final String C_VAL_HOST = "http://CUSTOMER-VALIDATION-SERVICE/c-val-service/api";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void send(String to) {
       try {
           String fooResourceUrl = C_VAL_HOST +"/otp/send";
           URI uri = UriComponentsBuilder.fromUriString(fooResourceUrl)
                   .queryParam("to", to)
                   .build()
                   .toUri();
           LOGGER.info(uri.toString());
           ResponseEntity<Object> response = restTemplate.postForEntity(uri ,null, Object.class);
           LOGGER.info(response.toString());
       }catch (Exception e){
           System.out.println(e.getMessage());
       }
    }
}
