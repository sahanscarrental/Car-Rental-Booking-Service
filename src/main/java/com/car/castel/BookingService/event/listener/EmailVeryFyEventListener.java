package com.car.castel.BookingService.event.listener;

import com.car.castel.BookingService.config.RabbitEmailVeryFyConfig;
import com.car.castel.BookingService.entity.VehicleDriver;
import com.car.castel.BookingService.event.modal.EmailVerifiedEvent;
import com.car.castel.BookingService.service.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class is listening for Email verification success event
 */
@Slf4j
@Component
public class EmailVeryFyEventListener {
    @Autowired
    private DriverService driverService;

    @RabbitListener(queues = RabbitEmailVeryFyConfig.QUEUE)
    public void consumeMessageFromQueue(EmailVerifiedEvent emailVerifiedEvent){
        log.info("Email is verified for  the driver having email : {}", emailVerifiedEvent.toString());
        VehicleDriver byEmail = driverService.getByEmail(emailVerifiedEvent.getTo());
        byEmail.setEmailVerified(true);
        driverService.update(byEmail.getId(), byEmail);
    }
}
