package com.car.castel.BookingService.repository;

import com.car.castel.BookingService.entity.VehicleDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<VehicleDriver, UUID> {
    Optional<VehicleDriver> findByEmail(@Email String email);

    /** get all drivers with new drivers at the beginning on the list
     *
     * @return list of drivers
     */
    List<VehicleDriver> findAllByOrderByCreatedDateDesc();
}
