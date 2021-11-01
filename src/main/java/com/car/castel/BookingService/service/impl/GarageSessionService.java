package com.car.castel.BookingService.service.impl;

import com.car.castel.BookingService.entity.Garage;
import com.car.castel.BookingService.entity.GarageSession;
import com.car.castel.BookingService.repository.GarageSessionRepository;
import com.car.castel.BookingService.service.CRUDServices;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GarageSessionService implements CRUDServices<GarageSession> {

    @Autowired
    private GarageSessionRepository garageSessionRepository;


    @Override
    public GarageSession create(GarageSession garageSession) {
        return garageSessionRepository.save(garageSession);
    }

    @Override
    public GarageSession update(UUID uuid, GarageSession garageSession) {
        Optional<GarageSession> garageSession1 = garageSessionRepository.findById(uuid);
        if (garageSession1.isPresent()){
            return  garageSessionRepository.save(garageSession);
        }else {
            throw new EntityNotFoundException(Garage.class,"id",uuid.toString());
        }
    }

    @Override
    public GarageSession get(UUID uuid) {
        Optional<GarageSession> garageSession1 = garageSessionRepository.findById(uuid);
        if (garageSession1.isPresent()){
            return  garageSession1.get();
        }else {
            throw new EntityNotFoundException(Garage.class,"id",uuid.toString());
        }
    }

    @Override
    public void delete(UUID uuid) {
        Optional<GarageSession> garageSession1 = garageSessionRepository.findById(uuid);
        if (garageSession1.isPresent()){
           garageSessionRepository.delete(garageSession1.get());
        }else {
            throw new EntityNotFoundException(Garage.class,"id",uuid.toString());
        }
    }

    @Override
    public List<GarageSession> createAll(List<GarageSession> list) {
        return list.stream().parallel().map(this::create).collect(Collectors.toList());
    }

    @Override
    public List<GarageSession> getAll() {
        return garageSessionRepository.findAll();
    }
}
