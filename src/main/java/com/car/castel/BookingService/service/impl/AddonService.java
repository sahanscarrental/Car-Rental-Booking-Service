package com.car.castel.BookingService.service.impl;

import com.car.castel.BookingService.entity.Addon;
import com.car.castel.BookingService.entity.BookingRecord;
import com.car.castel.BookingService.repository.AddonRepository;
import com.car.castel.BookingService.repository.BookingRecordRepository;
import com.car.castel.BookingService.service.CRUDServices;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import com.car.castel.BookingService.web.exception.ExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AddonService implements CRUDServices<Addon> {

    @Autowired
    private AddonRepository addonRepository;

    @Autowired
    private BookingRecordRepository bookingRecordRepository;

    @Override
    public Addon create(Addon addon) {
        return addonRepository.save(addon);
    }

    @Override
    public Addon update(UUID uuid, Addon addon) {
        Optional<Addon> addon1 = addonRepository.findById(uuid);
        if (addon1.isPresent()){
            return addonRepository.save(addon);
        }else {
            throw new EntityNotFoundException(Addon.class,"id",uuid.toString());
        }
    }

    @Override
    public Addon get(UUID uuid) {
        Optional<Addon> addon = addonRepository.findById(uuid);
        if (addon.isPresent()){
            return addon.get();
        }else {
            throw new EntityNotFoundException(Addon.class,"id",uuid.toString());
        }
    }

    @Override
    public void delete(UUID uuid) {
        Optional<Addon> addon = addonRepository.findById(uuid);
        if (addon.isPresent()){
            ArrayList<Addon> addonList = new ArrayList<Addon>(
                    Arrays.asList(addon.orElse(null)));
            List<BookingRecord> bookingRecords = this.bookingRecordRepository.findAllByAddonsIn(addonList);
            if (bookingRecords != null && bookingRecords.size() > 0) {
                throw new ExceptionWithMessage("Can not remove Addons which have current or previous bookings");
            }
            addonRepository.delete(addon.get());
        }else {
            throw new EntityNotFoundException(Addon.class,"id",uuid.toString());
        }
    }

    @Override
    public List<Addon> createAll(List<Addon> list) {
        return list.stream().parallel().map(this::create).collect(Collectors.toList());
    }

    @Override
    public List<Addon> getAll() {
        return addonRepository.findAll();
    }

    public List<Addon> getAllAvailable() {
        return addonRepository.findAvailableAddOns();
    }
}
