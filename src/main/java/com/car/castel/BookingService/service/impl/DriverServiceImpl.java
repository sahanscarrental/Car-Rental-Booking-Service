package com.car.castel.BookingService.service.impl;

import com.car.castel.BookingService.entity.VehicleDriver;
import com.car.castel.BookingService.entity.auth.ERole;
import com.car.castel.BookingService.entity.auth.Role;
import com.car.castel.BookingService.entity.auth.User;
import com.car.castel.BookingService.entity.auth.UserStatus;
import com.car.castel.BookingService.entity.type.DriverState;
import com.car.castel.BookingService.event.EventHandlerProvider;
import com.car.castel.BookingService.event.EventInvoker;
import com.car.castel.BookingService.event.EventType;
import com.car.castel.BookingService.event.modal.DMVSuspendedDriverEvent;
import com.car.castel.BookingService.event.modal.OTPSendEventModal;
import com.car.castel.BookingService.repository.DriverRepository;
import com.car.castel.BookingService.repository.auth.RoleRepository;
import com.car.castel.BookingService.service.BandedDriver;
import com.car.castel.BookingService.service.DriverService;
import com.car.castel.BookingService.service.FakeClaim;
import com.car.castel.BookingService.service.auth.UserService;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import com.car.castel.BookingService.web.exception.ExceptionWithMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements DriverService {


    private final static Logger LOGGER = LoggerFactory.getLogger(DriverService.class);


    public static final String FILE_SERVICE_HOST = "http://file-service/api/image";

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BandedDriver bandedDriver;

    /**
     * holds set of post events and invoke when
     * invoke is called
     */
    private EventInvoker eventInvoker;

    /**
     * provide  event brokers
     */
    @Autowired
    EventHandlerProvider eventHandlerProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * This method generate a REG no for the given VehicleDriver
     * @param vehicleDriver instance of VehicleDriver
     * @return REG NO
     */
    private String generateRegNo(VehicleDriver vehicleDriver) {
        long leftLimit = 1000L;
        long rightLimit = 9999L;
        long l = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
        return "CC-REG-"+vehicleDriver.getLicenceNo()+""+l;
    }


    @Override
    public VehicleDriver create(VehicleDriver vehicleDriver) {

        /**
         * Checking the drive is DMV suspended or not
         */
        if (bandedDriver.isIn(vehicleDriver.getLicenceNo())){
            this.detectDMVSuspended(vehicleDriver);
        }

        vehicleDriver.setRegNo(this.generateRegNo(vehicleDriver));

        /**
         * Creating a new USER account for the driver
         */
        User user = new User();
        user.setUserName(vehicleDriver.getEmail());
        user.setUserStatus(UserStatus.DEACTIVATED);
        String encode = passwordEncoder.encode(vehicleDriver.getPassword());
        user.setPassword(encode);
        List<ERole> eRoleList = new ArrayList<>();
        eRoleList.add(ERole.ROLE_DRIVER);
        Set<Role> roleList = eRoleList
                .stream()
                .map(eRole -> {
                    Optional<Role> byName = roleRepository.findByName(eRole);
                    if (byName.isPresent()) {
                        return byName.get();
                    } else {
                        throw new EntityNotFoundException(Role.class, "id", eRole.toString());
                    }
                })
                .collect(Collectors.toSet());
        user.setRoles(roleList);
        User savedUser = userService.create(user);


        vehicleDriver.setUser(savedUser);
        vehicleDriver.setPassword(encode);
        VehicleDriver save = driverRepository.save(vehicleDriver);
        OTPSendEventModal otpSendEvent = OTPSendEventModal.builder().to(save.getEmail()).build();

        /**
         * event for OTP sending
         */
        eventInvoker = new EventInvoker();
        eventInvoker.addEvent(eventHandlerProvider.getBroker(EventType.OTP_SEND, otpSendEvent));

        eventInvoker.invoke();
        return save;
    }

    @Override
    public VehicleDriver update(UUID uuid, VehicleDriver vehicleDriver) {
        Optional<VehicleDriver> driver = driverRepository.findById(uuid);
        if (driver.isPresent()){
            VehicleDriver driver1 = driver.get();
            if (vehicleDriver.getDriverState().equals(DriverState.ADMIN_APPROVED)){ // when admin approve the register request
                User user = driver1.getUser();
                user.setUserStatus(UserStatus.ACTIVATED);
                userService.update(user.getId(), user);
            }else if (vehicleDriver.getDriverState().equals(DriverState.ADMIN_REJECTED)){ // when admin reject the register request
                User user = driver1.getUser();
                user.setUserStatus(UserStatus.DEACTIVATED);
                userService.update(user.getId(), user);
            }
            return  driverRepository.save(vehicleDriver);
        }else {
            throw new EntityNotFoundException(VehicleDriver.class,"id",uuid.toString());
        }
    }

    @Override
    public VehicleDriver get(UUID uuid) {
        Optional<VehicleDriver> driver = driverRepository.findById(uuid);
        if (driver.isPresent()){
            return  driver.get();
        }else {
            throw new EntityNotFoundException(VehicleDriver.class,"id",uuid.toString());
        }
    }

    @Override
    public void delete(UUID uuid) {
        Optional<VehicleDriver> driver = driverRepository.findById(uuid);
        if (driver.isPresent()){
            VehicleDriver driver1 = driver.get();
            driver1.setDriverState(DriverState.DELETED);
            driverRepository.save(driver1);
        }else {
            throw new EntityNotFoundException(VehicleDriver.class,"id",uuid.toString());
        }
    }

    @Override
    public List<VehicleDriver> createAll(List<VehicleDriver> list) {
        return list.stream().parallel().map(this::create).collect(Collectors.toList());
    }

    @Override
    public List<VehicleDriver> getAll() {
        return driverRepository.findAllByOrderByCreatedDateDesc()
                .stream()
                .filter(vehicleDriver -> vehicleDriver.getDriverState() != DriverState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean isEmailTaken(String email) {
        return driverRepository.findByEmail(email).isPresent();
    }

    @Override
    public VehicleDriver getByEmail(String email) {
        Optional<VehicleDriver> driver = driverRepository.findByEmail(email);
        return  driver.get();
    }

    /**
     * This method create event, when DMV suspended driver is detecting
     * @param vehicleDriver instance if VehicleDriver
     */
    private void detectDMVSuspended(VehicleDriver vehicleDriver){
        EventInvoker eventInvoker = new EventInvoker();
        DMVSuspendedDriverEvent dmvSuspendedDriverEvent = DMVSuspendedDriverEvent
                .builder()
                .frontImageId(vehicleDriver.getFaceImageId())
                .regNo(vehicleDriver.getLicenceNo())
                .time(new Date())
                .build();
        eventInvoker.addEvent(eventHandlerProvider.getBroker(EventType.DMV_SUSPENDED, dmvSuspendedDriverEvent));
        eventInvoker.invoke();
        throw new ExceptionWithMessage("You are suspended by Department of Motor Vehicles (DMV)");
    }
}
