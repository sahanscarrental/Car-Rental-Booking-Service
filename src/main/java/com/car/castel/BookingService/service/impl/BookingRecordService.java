package com.car.castel.BookingService.service.impl;

import com.car.castel.BookingService.entity.Addon;
import com.car.castel.BookingService.entity.BookingRecord;
import com.car.castel.BookingService.entity.Vehicle;
import com.car.castel.BookingService.entity.VehicleDriver;
import com.car.castel.BookingService.entity.type.Currency;
import com.car.castel.BookingService.entity.type.*;
import com.car.castel.BookingService.event.EventHandlerProvider;
import com.car.castel.BookingService.event.EventInvoker;
import com.car.castel.BookingService.event.EventType;
import com.car.castel.BookingService.event.modal.CustomerBookingEvent;
import com.car.castel.BookingService.event.modal.DMVSuspendedDriverEvent;
import com.car.castel.BookingService.event.modal.DropTimeExtendedEvent;
import com.car.castel.BookingService.repository.AddonRepository;
import com.car.castel.BookingService.repository.BookingRecordRepository;
import com.car.castel.BookingService.repository.DriverRepository;
import com.car.castel.BookingService.service.BandedDriver;
import com.car.castel.BookingService.service.BookingService;
import com.car.castel.BookingService.service.DriverService;
import com.car.castel.BookingService.service.FakeClaim;
import com.car.castel.BookingService.utils.BookingEventType;
import com.car.castel.BookingService.utils.MyDateUtils;
import com.car.castel.BookingService.web.dto.request.BookingRequest;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import com.car.castel.BookingService.web.exception.ExceptionWithMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@EnableScheduling
@Service
public class BookingRecordService implements BookingService {

    @Autowired
    private BookingRecordRepository bookingRecordRepository;

    @Autowired
    private AddonRepository addonRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverService driverService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private BandedDriver bandedDriver;

    @Autowired
    private FakeClaim fakeClaim;

    /**
     * provide  event brokers
     */
    @Autowired
    EventHandlerProvider eventHandlerProvider;

    public Boolean checkAddonAvailability(Addon addon, Date from, Date to){
        Collection<BookingRecordState> collection = new ArrayList<>();
        collection.add(BookingRecordState.PICKED);
        collection.add(BookingRecordState.PENDING);
        List<BookingRecord> bookingRecords = bookingRecordRepository.findAllByBookingRecordStateIn(collection);
        return bookingRecords
                .stream()
                .filter(bookingRecord -> {
                    List<Addon> addons = bookingRecord.getAddons();
                    if (addons.size() == 0){ return false; }
                    boolean anyMatch = addons.stream().anyMatch(addon1 -> {
                        boolean b = addon1.getId().equals(addon.getId());
                        return b;
                    });
                    return anyMatch;
                })
                .noneMatch(bookingRecord -> MyDateUtils.isXBetweenY(from, to, bookingRecord.getPickUpTime(), bookingRecord.getDropTime()));
    }


    /**
     * This is executing once per day to check the
     * non picking bookings
     */
    @Scheduled(fixedRate = 80000000)
    public void checkNonPickup() {
        List<BookingRecord> unPickedToday = this.getUnPickedToday();
        unPickedToday
                .stream()
                .parallel()
                .forEach(bookingRecord -> {
                    VehicleDriver vehicleDriver = bookingRecord.getVehicleDriver();
                    vehicleDriver.setDriverState(DriverState.BLACK_LISTED);
                    driverService.update(vehicleDriver.getId(), vehicleDriver);
                    log.info("The driver with email {} adding to black-list", vehicleDriver.getEmail());
                });

    }

    /**
     * This method check the age with the given vehicle category
     * @param dob Birthday
     * @param vehicleCategory instance of VehicleCategory
     * @return boolean value
     */
    public Boolean isValidAge(Date dob, VehicleCategory vehicleCategory) {
        Map<TimeUnit, Long> timeUnitLongMap = MyDateUtils.computeDiff(dob, new Date());
        Long days = timeUnitLongMap.get(TimeUnit.DAYS);
        if (!vehicleCategory.equals(VehicleCategory.SMALL_TOWN_CARS)){
            return days  > 365 * 25;
        }
        return true;
    }

    public Boolean isValidTimeExtend(Date extendedDate) {
        boolean result = true;
       if (extendedDate != null) {
           Calendar extendedInstance = Calendar.getInstance();
           Calendar calNextInstance = Calendar.getInstance();
           extendedInstance.setTime(extendedDate);

           calNextInstance.set(extendedInstance.get(Calendar.YEAR),
                   extendedInstance.get(Calendar.MONTH), extendedInstance.get(Calendar.DAY_OF_MONTH), 16,00);

           Date timeToReturn = calNextInstance.getTime();
           // check if the extended time is equal or less than the deadline of 4:00 P.M of the same day
           if (timeToReturn != null) {
               result = extendedDate.before(timeToReturn) || extendedDate.equals(timeToReturn);
           }

       }
       return result;
    }

    /**
     * This method calculate the price for the booking
     * @param pickUpTime pickUpTime
     * @param dropTime dropTime
     * @param costPerDay costPerDay
     * @return total cost
     */
    public Double calculateCost (Date pickUpTime ,Date dropTime , Double costPerDay) {
        Map<TimeUnit, Long> timeUnitLongMap = MyDateUtils.computeDiff(pickUpTime, dropTime);
        Long days = timeUnitLongMap.get(TimeUnit.DAYS);
        Long hours = timeUnitLongMap.get(TimeUnit.HOURS);
        Long mints = timeUnitLongMap.get(TimeUnit.MINUTES);

        log.info("DAYS: {}, HOURS: {}, MINUTES: {}", days, hours, mints);
        Double cost = (double) 0;

        if (days < 1 && hours < 5) {
            throw new ExceptionWithMessage("Minimum booking duration is 5 hours");
        }

        if (days > 14 || (days == 14  && mints > 0)) {
            throw new ExceptionWithMessage("Maximum booking duration is 2 weeks [14 x 24 hours]");
        }

        if (days < 1 && hours <= 5){
            cost = costPerDay / 2; // Vehicle’s half a day’s rental
        }

        if (days < 1 && hours == 5 && mints > 0) {
            // book for 5 hours with minutes then should be charged for whole day
            cost = costPerDay;
        }

        if (days < 1 && hours > 5){
            cost = costPerDay ; // Vehicle’s day’s rental
        }

        if (days >= 1 ){
            cost = cost + (days * costPerDay); // calculate the cost for all days
            if (hours > 5){
                cost = cost + costPerDay; // add the last day cost
            }
        }

        return cost;
    }

    @Override
    public List<BookingRecord> getUnPickedToday() {
        Date morning = new Date();
        morning.setTime(0);

        Date night = new Date();
        night.setTime(23);
        return bookingRecordRepository.findAllByBookingRecordStateAndPickUpTimeBetween(
          BookingRecordState.PENDING, morning, night
        );
    }

    @Override
    public List<BookingRecord> getByDateRange(Date from , Date to) {
        return bookingRecordRepository.findAllByCreatedDateBeforeAndCreatedDateAfter(new Timestamp(from.getTime()), new Timestamp(to.getTime()));
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
        throw new ExceptionWithMessage("The driver suspended by Department of Motor Vehicles (DMV)");
    }

    @Override
    public BookingRecord create(BookingRecord bookingRecord){
        DateFormat outputFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Vehicle vehicle = vehicleService.get(bookingRecord.getVehicle().getId());
        VehicleDriver vehicleDriver = driverService.get(bookingRecord.getVehicleDriver().getId());

        /**
         * Checking the drive is DMV suspended or not
         */
        if(fakeClaim.isIn(vehicleDriver.getLicenceNo())){
            throw new ExceptionWithMessage("The driver has made fraudulent claims for car accidents");
        }

        /**
         * Checking the drive is DMV suspended or not
         */
        if (bandedDriver.isIn(vehicleDriver.getLicenceNo())){
            this.detectDMVSuspended(vehicleDriver);
        }

        /**
         * Prohibited if driver is blacklisted
         */
        if (vehicleDriver.getDriverState().equals(DriverState.BLACK_LISTED)){
            throw new ExceptionWithMessage("You are blacklisted!, You can not make any booking in our service");
        }

        /**
         * Check all addons are available or not
         */
        List<Addon> addons = bookingRecord.getAddons();
        addons.stream().parallel().forEach(addon -> {
            Boolean availability = this.checkAddonAvailability(addon, bookingRecord.getPickUpTime(), bookingRecord.getDropTime());
            if (!availability){
                throw new ExceptionWithMessage("The addon "+addon.getName()+" is not available in this period");
            }
        });

        String pickupTimeText = bookingRecord.getPickUpTime().toString();
        String dropTimeText = bookingRecord.getDropTime().toString();
        Date pickUpTime = this.getFormattedDate( bookingRecord.getPickUpTime().toString());
        Date dropTime = this.getFormattedDate( bookingRecord.getDropTime().toString());
        List<BookingRecord> allByVehicleAndBookingRecordState = bookingRecordRepository.findAllByVehicleAndBookingRecordState(vehicle, BookingRecordState.PENDING);
        boolean anyMatch = allByVehicleAndBookingRecordState
                .stream()
                .parallel()
                .anyMatch(bookingRecord1 -> MyDateUtils.isXBetweenY(pickUpTime,dropTime,  bookingRecord1.getPickUpTime(),bookingRecord1.getDropTime()));
        if (anyMatch){
            throw new ExceptionWithMessage("This vehicle is not available in this period");
        }

        Double cost = this.calculateCost(pickUpTime, dropTime, vehicle.getCostPerDay());
        log.info("cost: {}", cost);
        bookingRecord.setCost(cost);
        bookingRecord.setCurrency(Currency.LKR);
        bookingRecord.setPaymentState(PaymentState.PENDING);
        bookingRecord.setPaymentType(PaymentType.ON_GARAGE);

        if (!isValidAge(vehicleDriver.getDob(), vehicle.getVehicleCategory())){
            throw new ExceptionWithMessage("Your age is not permitted to book this vehicle");
        }
        if (pickUpTime.getHours() > 21 || pickUpTime.getHours() < 11){
            throw new ExceptionWithMessage("We are open from 8 AM to 4pm");
        }

        if (dropTime.getHours() > 21 || dropTime.getHours() < 11){
            throw new ExceptionWithMessage("We are open from 8 AM to 4pm");
        }


        if (bookingRecord.getReturnAtNight() == null){
            bookingRecord.setReturnAtNight(false);
        }

        bookingRecord.setBookingRecordState(BookingRecordState.PENDING);
        bookingRecord.setVehicleDriver(vehicleDriver);
        bookingRecord.setVehicle(vehicle);
        return bookingRecordRepository.save(bookingRecord);
    }

    public BookingRecord createFromDTO(BookingRequest bookingRequest){
        DateFormat outputFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Vehicle vehicle = vehicleService.get(bookingRequest.getVehicle().getId());
        VehicleDriver vehicleDriver = driverService.get(bookingRequest.getVehicleDriver().getId());

        /**
         * Checking the drive is DMV suspended or not
         */
        /*if(fakeClaim.isIn(vehicleDriver.getLicenceNo())){
            throw new ExceptionWithMessage("The driver has made fraudulent claims for car accidents");
        }*/

        /**
         * Checking the drive is DMV suspended or not
         */
        if (bandedDriver.isIn(vehicleDriver.getLicenceNo())){
            this.detectDMVSuspended(vehicleDriver);
        }

        /**
         * Prohibited if driver is blacklisted
         */
        if (vehicleDriver.getDriverState().equals(DriverState.BLACK_LISTED)){
            throw new ExceptionWithMessage("You are blacklisted!, You can not make any booking in our service");
        }

        String pickupTimeText = bookingRequest.getPickUpTime().toString();
        String dropTimeText = bookingRequest.getDropTime().toString();
        Date pickUpTime = this.getFormattedDate( bookingRequest.getPickUpTime().toString());
        Date dropTime = this.getFormattedDate( bookingRequest.getDropTime().toString());

        List<BookingRecord> allByVehicleAndBookingRecordState = bookingRecordRepository.findAllByVehicleAndBookingRecordState(vehicle, BookingRecordState.PENDING);
        boolean anyMatch = allByVehicleAndBookingRecordState
                .stream()
                .parallel()
                .anyMatch(bookingRecord1 -> MyDateUtils.isXBetweenY(pickUpTime,dropTime,bookingRecord1.getPickUpTime(),bookingRecord1.getDropTime()));
        if (anyMatch){
            throw new ExceptionWithMessage("This vehicle is not available in this period");
        }

        Double cost = this.calculateCost(pickUpTime, dropTime, vehicle.getCostPerDay());
        log.info("cost: {}", cost);
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setPickUpTime(pickUpTime);
        bookingRecord.setDropTime(dropTime);
        bookingRecord.setCost(cost);
        bookingRecord.setCurrency(vehicle.getCurrency());
        bookingRecord.setPaymentState(PaymentState.PENDING);
        bookingRecord.setPaymentType(PaymentType.ON_GARAGE);

        if (!isValidAge(vehicleDriver.getDob(), vehicle.getVehicleCategory())){
            throw new ExceptionWithMessage("Your age is not permitted to book this vehicle");
        }

        // So pickup hours should be from 8 to 18
        if ((pickUpTime.getHours() < 8) || (pickUpTime.getHours() > 18 || (pickUpTime.getHours() == 18 && pickUpTime.getMinutes() > 0))) {
            throw new ExceptionWithMessage("Invalid pickup time, We are open from 8 AM to 6pm");
        }

        // 7 - 8.00 A.M, 17 - 6.00 p.m
        if (dropTime.getHours() < 8 || (dropTime.getHours() > 18 || (dropTime.getHours() == 18 && dropTime.getMinutes() > 0))) {
            throw new ExceptionWithMessage("Invalid drop off time, We are open from 8 AM to 6pm");
        }


        if (bookingRequest.getReturnAtNight() == null){
            bookingRequest.setReturnAtNight(false);
        } else {
            bookingRecord.setReturnAtNight(bookingRequest.getReturnAtNight());
        }

        bookingRecord.setBookingRecordState(BookingRecordState.PENDING);
        bookingRecord.setVehicleDriver(vehicleDriver);
        bookingRecord.setVehicle(vehicle);

        //include the addons
        List<Addon> addonList = bookingRequest.getAddons();
        for (Addon addon: addonList) {
            bookingRecord.addAddOn(addon);
        }
        BookingRecord createdBookingRecord = bookingRecordRepository.save(bookingRecord);

        // increase the booked addons and decrease the available addons when the booking is created
        if (addonList != null && addonList.size() > 0) {
            for (Addon addon : addonList) {
                addon.setAvailableCount(addon.getAvailableCount() - 1);
                addon.setBookedCount(addon.getBookedCount() + 1);
                this.addonRepository.save(addon);
            }
        }
        // to send email to the driver for vehicle drop off
        this.createBookingChangeEvent(createdBookingRecord, BookingEventType.BOOKING_CREATED, EventType.BOOKING_CREATED);

        return createdBookingRecord;
    }

    /**
     * run at every 30 minutes
     */
    @Override
    @Scheduled(fixedRate = 1000 * 60 * 30)
    public void scheduledCheckForBlackListDrivers() {
        log.info("Scanning for failed to pickup bookings ");
        // check for failed to pickup vehicles with a flexible period of 1 hour
        List<BookingRecord> failedToPickupList = this.bookingRecordRepository.findFailedToPickBookings(MyDateUtils.addOrSubtractHours(new Date(), -1));
        if (failedToPickupList != null) {
            if (failedToPickupList.size() > 0) {
                log.info("Failed to Pickup Bookings found: " + failedToPickupList.size());
                // black listing the drivers who failed to pick up the vehicles
                VehicleDriver blackListingDriver = null;
                for (BookingRecord bookingRecord : failedToPickupList) {
                    // Updating the driver status
                    blackListingDriver = bookingRecord.getVehicleDriver();
                    if (blackListingDriver != null) {
                        blackListingDriver.setDriverState(DriverState.BLACK_LISTED);
                        this.driverRepository.save(blackListingDriver);
                    } else {
                        log.info("Failed to Retrieve the driver for blacklisting for booking record: " + bookingRecord.getId()
                                + "vehicleNo: " + bookingRecord.getVehicle().getVehicleNo());
                    }
                    //updating the booking record status
                    bookingRecord.setBookingRecordState(BookingRecordState.FAILED_TO_PICK);
                    this.bookingRecordRepository.save(bookingRecord);
                    log.info("Booking record updated to failed to pick, ID: " + bookingRecord.getId() + " pickupTime: " + bookingRecord.getPickUpTime());
                    // release addons if available
                    this.releaseAddons(bookingRecord.getAddons());
                }
                log.info("Drivers blacklisting completed");
            } else {
                log.info("No failed-to-pickup booking records found.");
            }
        }
    }

    @Override
    public BookingRecord extendDropTime(UUID bookingRecordId, String extendedDropTime) {
        // check if the extended drop time is invalid
        if (bookingRecordId == null || extendedDropTime == null || extendedDropTime.isEmpty()) {
            throw new ExceptionWithMessage("required data is missing for extending the booking");
        }
        BookingRecord bookingRecord = this.bookingRecordRepository.findById(bookingRecordId).orElse(null);
        if (bookingRecord == null) {
            throw new ExceptionWithMessage("Can not find the Booking record for given booking ID: " + bookingRecord.toString());
        }
        Date updatedExtendedDate = this.getFormattedDate(extendedDropTime);
        // validate if extend time goes beyond 4:00 P.M
        if (!this.isValidTimeExtend(updatedExtendedDate)) {
            throw new ExceptionWithMessage("Booking can be extended only till 4:00 P.M");
        }
        if (updatedExtendedDate.before(bookingRecord.getDropTime())) {
            throw new ExceptionWithMessage("Extended Time should be later than Return time");
        }
        // check where there is any booking for the vehicle on next day
        List<BookingRecord> allByVehicleAndBookingRecordState = bookingRecordRepository.findAllByVehicleAndBookingRecordState(bookingRecord.getVehicle(), BookingRecordState.PENDING);
        boolean anyMatch = allByVehicleAndBookingRecordState
                .stream()
                .parallel()
                .anyMatch(bookingRecord2 -> {
                    Date dropTime = bookingRecord.getDropTime();
                    Date tomorrow = new Date();
                    DateUtils.addDays(tomorrow, 1);
                    return DateUtils.isSameDay(dropTime, tomorrow);
                });
        if (anyMatch){
            throw new ExceptionWithMessage("This vehicle is booked tomorrow, You can not extend time");
        }

        bookingRecord.setExtendedDropTime(updatedExtendedDate);
        BookingRecord updatedRecord = this.bookingRecordRepository.save(bookingRecord);
        log.info("Booking record Extended successfully");

        DropTimeExtendedEvent dropTimeExtendedEvent = DropTimeExtendedEvent
                .builder()
                .extendedTo(updatedExtendedDate)
                .driverEmail(bookingRecord.getVehicleDriver().getEmail())
                .driverName(bookingRecord.getVehicleDriver().getName())
                .driverPhoneNo(bookingRecord.getVehicleDriver().getPhoneNo())
                .vehicleNo(bookingRecord.getVehicle().getVehicleNo())
                .build();
        EventInvoker eventInvoker = new EventInvoker();
        eventInvoker.addEvent(eventHandlerProvider.getBroker(EventType.DROP_TIME_EXTEND, dropTimeExtendedEvent));
        eventInvoker.invoke();

        return updatedRecord;
    }

    @Override
    public void releaseAddons(List<Addon> addonList) {

        if (addonList == null || addonList.size() == 0) {
            return;
        }
        Addon updatingAddon = null;
        for (Addon addon : addonList) {
            updatingAddon = this.addonRepository.findById(addon.getId()).orElse(null);
            log.info("releasing: "+ updatingAddon.getName());
            updatingAddon.setAvailableCount(addon.getAvailableCount() + 1);
            updatingAddon.setBookedCount(addon.getBookedCount() - 1);
            this.addonRepository.save(updatingAddon);
        }
        log.info("Addons released: " + addonList.size());
    }

    @Override
    public void acquireAddons(List<Addon> addonList) {

        if (addonList == null || addonList.size() == 0) {
            return;
        }
        Addon updatingAddon = null;
        for (Addon addon : addonList) {
            updatingAddon = this.addonRepository.findById(addon.getId()).orElse(null);
            log.info("acquiring: "+ updatingAddon.getName());
            updatingAddon.setAvailableCount(addon.getAvailableCount() - 1);
            updatingAddon.setBookedCount(addon.getBookedCount() + 1);
            this.addonRepository.save(updatingAddon);
        }
        log.info("Addons acquired: " + addonList.size());

    }

    private Date getFormattedDate(String date) {
        Date formattedDate = new Date();
        if (date != null) {
            try {
                String editedDate = date.replace('T', ' ');
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                formattedDate = formatter.parse(editedDate);
                System.out.println("formattedDate: " + formattedDate);
            } catch (Exception e) {
                System.out.println("error occurred for date formatting: " + e.getMessage());
            }
        }
        return formattedDate;
    }

    @Override
    public BookingRecord update(UUID uuid, BookingRecord updatedBookingRecord) {
        DateFormat outputFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Optional<BookingRecord> bookingRecord1 = bookingRecordRepository.findById(uuid);
        if (bookingRecord1.isPresent()){
            try{
                if (updatedBookingRecord.getPickUpTime() != null) updatedBookingRecord.setPickUpTime(outputFormat.parse(updatedBookingRecord.getPickUpTime().toString()));
                if (updatedBookingRecord.getDropTime() != null) updatedBookingRecord.setDropTime(outputFormat.parse(updatedBookingRecord.getDropTime().toString()));
                if (updatedBookingRecord.getExtendedDropTime() != null) updatedBookingRecord.setExtendedDropTime(outputFormat.parse(updatedBookingRecord.getExtendedDropTime().toString()));
            }catch (Exception e){
                e.printStackTrace();
            }
            if (bookingRecord1.get().getAddons().size() ==0 && updatedBookingRecord.getAddons().size() > 0){
                List<Addon> addons = updatedBookingRecord.getAddons();
                addons.forEach(addon -> {
                    Boolean availability = this.checkAddonAvailability(addon, updatedBookingRecord.getPickUpTime(), updatedBookingRecord.getDropTime());
                    if (!availability){
                        throw new ExceptionWithMessage("The addon "+addon.getName()+" is not available in this period");
                    }
                });
            }

            if (bookingRecord1.get().getBookingRecordState() == BookingRecordState.PENDING && updatedBookingRecord.getBookingRecordState() == BookingRecordState.PICKED){
                VehicleDriver vehicleDriver = updatedBookingRecord.getVehicleDriver();

                if (!vehicleDriver.getEmailVerified()) {
                    throw new ExceptionWithMessage("Driver Email is not verified");
                }

                //check if the pickup date time is valid
                Map<TimeUnit, Long> timeUnitLongMap = MyDateUtils.computeDiff(bookingRecord1.get().getPickUpTime(), new Date());
                Long days = timeUnitLongMap.get(TimeUnit.DAYS);
                Long minutes = timeUnitLongMap.get(TimeUnit.MINUTES);
                if (days != 0) {
                    throw new ExceptionWithMessage("Can not pick up vehicle other than the pickup date");
                }
                if (minutes < 0) {
                    throw new ExceptionWithMessage("Need to wait up until the pickup time");
                }

                /**
                 * Checking the drive has fake claims (external database)
                 */
                if(fakeClaim.isIn(vehicleDriver.getLicenceNo())){
                    // need to cancel eka booking
                    BookingRecord fakeClaimBookingRecord = bookingRecord1.orElse(null);
                    if (fakeClaimBookingRecord != null) {
                        fakeClaimBookingRecord.setBookingRecordState(BookingRecordState.CANCELED);
                        bookingRecordRepository.save(fakeClaimBookingRecord);
                        log.info("Booking is cancelled since system detect fraud claims for driver");
                    }
                    throw new ExceptionWithMessage("The driver has made fraudulent claims for car accidents");
                }

                /**
                 * Checking the drive is DMV suspended or not (CSV)
                 */
                if (bandedDriver.isIn(vehicleDriver.getLicenceNo())){
                    this.detectDMVSuspended(vehicleDriver);
                }

                /**
                 * Prohibited if driver is blacklisted
                 */
                if (vehicleDriver.getDriverState().equals(DriverState.BLACK_LISTED)){
                    throw new ExceptionWithMessage("You are blacklisted!, You can not make any booking in our service");
                }

                // to send email to the driver for vehicle drop off
                this.createBookingChangeEvent(updatedBookingRecord, BookingEventType.VEHICLE_PICKED_UP, EventType.VEHICLE_PICKED_UP);
            }

            List<Addon> updatedAddonList = updatedBookingRecord.getAddons();
            List<Addon> existingAddonList = bookingRecord1.get().getAddons();
            List<Addon> removedAddonList =new ArrayList<>();
            List<Addon> addedAddonList =new ArrayList<>();

            if (updatedAddonList != null && existingAddonList != null) {

            updatedAddonList.sort(Comparator.comparing(Addon::getName));
            existingAddonList.sort(Comparator.comparing(Addon::getName));

                List<Addon> firstList =  new ArrayList<>(existingAddonList);
                List<Addon> secondLIst = new ArrayList<>(updatedAddonList);

                // check if the addon list remain unchanged
                if (!updatedAddonList.equals(existingAddonList)) {

                    // removed addons
                    firstList.removeAll(secondLIst);
                    removedAddonList = firstList;

                    // added addons
                    firstList = existingAddonList;
                    secondLIst.removeAll(firstList);
                    addedAddonList = secondLIst;

                    // acquire or release the addons
                    this.acquireAddons(addedAddonList);
                    this.releaseAddons(removedAddonList);
                } else {
                    log.info("addons remain unchanged");
                }
            }

            Date extendedDropTime = updatedBookingRecord.getExtendedDropTime();
            if (updatedBookingRecord.getBookingRecordState() == BookingRecordState.COMPLETED){
                VehicleDriver vehicleDriver = updatedBookingRecord.getVehicleDriver();
                vehicleDriver.incrementBookingCount();
                driverService.update(vehicleDriver.getId(), vehicleDriver);
                List<Addon> addonList = updatedBookingRecord.getAddons();
                // releasing the addons
                this.releaseAddons(addonList);
                // to send email to the driver for vehicle drop off
                this.createBookingChangeEvent(updatedBookingRecord, BookingEventType.VEHICLE_DROP_OFF, EventType.VEHICLE_DROP_OFF);
            } else if (updatedBookingRecord.getBookingRecordState() == BookingRecordState.CANCELED){
                // check is it too close to cancel
                Map<TimeUnit, Long> timeUnitLongMap = MyDateUtils.computeDiff(bookingRecord1.get().getPickUpTime(), new Date());
                Long days = timeUnitLongMap.get(TimeUnit.DAYS);
                if (days > -2) {
                    throw new ExceptionWithMessage("Can not Cancel since Booking is within two days");
                }
                this.releaseAddons(updatedBookingRecord.getAddons());
                // to send email to the driver for boking cancelled
                this.createBookingChangeEvent(updatedBookingRecord, BookingEventType.BOOKING_CANCELLED, EventType.BOOKING_CANCELLED);
            } else if (extendedDropTime != null && bookingRecord1.get().getExtendedDropTime() == null){
                List<BookingRecord> allByVehicleAndBookingRecordState = bookingRecordRepository.findAllByVehicleAndBookingRecordState(updatedBookingRecord.getVehicle(), BookingRecordState.PENDING);
                boolean anyMatch = allByVehicleAndBookingRecordState
                        .stream()
                        .parallel()
                        .anyMatch(bookingRecord2 -> {
                            Date dropTime = updatedBookingRecord.getDropTime();
                            Date tomorrow = new Date();
                            DateUtils.addDays(tomorrow, 1);
                            return DateUtils.isSameDay(dropTime, tomorrow);
                        });
                if (anyMatch){
                    throw new ExceptionWithMessage("This vehicle is booked tomorrow, You can not extend time");
                }
                if (!DateUtils.isSameDay(extendedDropTime, updatedBookingRecord.getDropTime())){
                    throw new ExceptionWithMessage("You can only extend to 4 pm");
                }
                if (updatedBookingRecord.getVehicleDriver().getSuccessBookings() < 1 && (extendedDropTime.getHours() > 21)){
                    throw new ExceptionWithMessage("You can only extend to 4 pm!. This is your 1 st Booking. You can" +
                            "return Vehicle after 6pm since your 2 nd Booking");
                }
                DropTimeExtendedEvent dropTimeExtendedEvent = DropTimeExtendedEvent
                        .builder()
                        .extendedTo(extendedDropTime)
                        .driverEmail(updatedBookingRecord.getVehicleDriver().getEmail())
                        .driverName(updatedBookingRecord.getVehicleDriver().getName())
                        .driverPhoneNo(updatedBookingRecord.getVehicleDriver().getPhoneNo())
                        .vehicleNo(updatedBookingRecord.getVehicle().getVehicleNo())
                        .build();
                EventInvoker eventInvoker = new EventInvoker();
                eventInvoker.addEvent(eventHandlerProvider.getBroker(EventType.DROP_TIME_EXTEND, dropTimeExtendedEvent));
                eventInvoker.invoke();
            }
            return bookingRecordRepository.save(updatedBookingRecord);
        }else {
            throw new EntityNotFoundException(BookingRecord.class,"id",uuid.toString());
        }
    }

    /**
     * generate a booking event to send email the driver
     * @param bookingRecord
     * @param bookingEventType
     * @param eventType
     */
    private void createBookingChangeEvent(BookingRecord bookingRecord, BookingEventType bookingEventType, EventType eventType) {
        CustomerBookingEvent customerBookingEvent = CustomerBookingEvent
                .builder()
                .bookingEventType(bookingEventType)
                .customerName(bookingRecord.getVehicleDriver().getName())
                .customerEmail(bookingRecord.getVehicleDriver().getEmail())
                .regNo(bookingRecord.getVehicle().getVehicleNo())
                .vehicleImageId(bookingRecord.getVehicle().getImageId())
                .time(new Date())
                .build();
        EventInvoker eventInvoker = new EventInvoker();
        eventInvoker.addEvent(eventHandlerProvider.getBroker(eventType, customerBookingEvent));
        eventInvoker.invoke();
    }

    @Override
    public BookingRecord get(UUID uuid) {
        Optional<BookingRecord> bookingRecord = bookingRecordRepository.findById(uuid);
        if (bookingRecord.isPresent()){
            return bookingRecord.get();
        }else {
            throw new EntityNotFoundException(BookingRecord.class,"id",uuid.toString());
        }
    }

    @Override
    public void delete(UUID uuid) {
        Optional<BookingRecord> bookingRecord = bookingRecordRepository.findById(uuid);
        if (bookingRecord.isPresent()){
            bookingRecordRepository.delete(bookingRecord.get());
        }else {
            throw new EntityNotFoundException(BookingRecord.class,"id",uuid.toString());
        }
    }

    @Override
    public List<BookingRecord> createAll(List<BookingRecord> list) {
        return list.stream().parallel().map(this::create).collect(Collectors.toList());
    }

    @Override
    public List<BookingRecord> getAll() {
        return bookingRecordRepository.findAll();
    }

    @Override
    public List<BookingRecord> getAll(UUID driverId) {
        return bookingRecordRepository.findAllByVehicleDriver(driverService.get(driverId));
    }
}
