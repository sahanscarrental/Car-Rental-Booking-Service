package com.car.castel.BookingService.repository;

import com.car.castel.BookingService.entity.Addon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddonRepository extends JpaRepository<Addon, UUID> {

    /**
     * select only the list of available Addons
     * @return a list of available addons
     */
    @Query("SELECT a from Addon a WHERE a.availableCount > 0")
    List<Addon> findAvailableAddOns();
}
