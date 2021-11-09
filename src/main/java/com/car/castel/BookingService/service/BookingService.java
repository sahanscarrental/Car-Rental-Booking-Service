package com.car.castel.BookingService.service;

import com.car.castel.BookingService.entity.Addon;
import com.car.castel.BookingService.entity.BookingRecord;
import com.car.castel.BookingService.web.dto.request.BookingRequest;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface BookingService extends CRUDServices<BookingRecord>{
    Double calculateCost (Date pickUpTime , Date dropTime , Double costPerDay);
    List<BookingRecord> getUnPickedToday();
    List<BookingRecord> getByDateRange(Date from , Date to);
    List<BookingRecord> getAll(UUID driver);

    /**
     * create a booking record from Booking DTO
     *
     * @param bookingRequest
     * @return
     */
    BookingRecord createFromDTO(BookingRequest bookingRequest);

    /**
     * run at every 10 minutes
     */
    public void scheduledCheckForBlackListDrivers();

    /**
     * update the given booking record for extended drop of time
     *
     * @param bookingRecordId
     * @param extendedDropTime
     * @return updated booking record
     */
    BookingRecord extendDropTime(UUID bookingRecordId,String extendedDropTime);

    /** release the used addons back after use or cancel the trip
     *
     * @param addonList
     */
    void releaseAddons(List<Addon> addonList);

    /** acquire the addons for driver for the trip
     *
     * @param addonList
     */
    void acquireAddons(List<Addon> addonList);

    /**
     * read periodically the XML file for the list of updated suspended drivers from DMV
     */
    void readDMVSuspendedDriversXML();
}
