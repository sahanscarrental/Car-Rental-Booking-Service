package com.car.castel.BookingService.repository;

import com.car.castel.BookingService.entity.Garage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GarageRepository extends JpaRepository<Garage, UUID> {
}
