package com.car.castel.BookingService.repository;

import com.car.castel.BookingService.entity.Addon;
import com.car.castel.BookingService.entity.BookingRecord;
import com.car.castel.BookingService.entity.Vehicle;
import com.car.castel.BookingService.entity.VehicleDriver;
import com.car.castel.BookingService.entity.type.BookingRecordState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRecordRepository extends JpaRepository<BookingRecord, UUID> {
    List<BookingRecord> findAllByVehicleDriver(VehicleDriver vehicleDriver);
    List<BookingRecord> findAllByVehicleAndBookingRecordState(Vehicle vehicle, BookingRecordState bookingRecordState);
    List<BookingRecord> findAllByBookingRecordStateAndPickUpTimeBetween(BookingRecordState bookingRecordState, Date pickUpTime, Date pickUpTime2);
    List<BookingRecord> findAllByBookingRecordStateIn(Collection<BookingRecordState> bookingRecordState);
    List<BookingRecord> findAllByCreatedDateBeforeAndCreatedDateAfter(Timestamp createdDate, Timestamp createdDate2);

    /**
     * find booking records by pickup time
     *
     * @param createdDate
     * @param createdDate2
     * @return
     */
    List<BookingRecord> findAllByPickUpTimeBeforeAndPickUpTimeAfter(Timestamp createdDate, Timestamp createdDate2);

    /**
     * get all records in ascending order by pickup time
     * @return
     */
    List<BookingRecord> findAllByOrderByPickUpTimeDesc();

    /**
     * find booking records for the given addon list
     * @param addons
     * @return
     */
    List<BookingRecord> findAllByAddonsIn(List<Addon> addons);

    /**
     * get all pending records and check if the given time has passed the pickup time
     * @param givenDate
     * @return
     */
    @Query("Select a from BookingRecord a where a.pickUpTime < :givenDate and a.bookingRecordState = 2")
    List<BookingRecord> findFailedToPickBookings(@Param("givenDate")Date givenDate);

    @Query("Select a from BookingRecord a where a.vehicle = :givenVehicle and (a.bookingRecordState = 2 or a.bookingRecordState = 3)")
    List<BookingRecord> findValidBookingsForVehicle(@Param("givenVehicle")Vehicle givenVehicle);

    /**
     * see if the given vehicle have any booking currently or previously
     *
     * @param givenVehicle
     * @return
     */
    @Query("Select a from BookingRecord a where a.vehicle = :givenVehicle")
    List<BookingRecord> findAnyBookingsForVehicle(@Param("givenVehicle") Vehicle givenVehicle);
}
