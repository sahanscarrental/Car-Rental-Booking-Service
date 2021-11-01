package com.car.castel.BookingService.repository;

import com.car.castel.BookingService.entity.GarageSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GarageSessionRepository extends JpaRepository<GarageSession, UUID> {
}
