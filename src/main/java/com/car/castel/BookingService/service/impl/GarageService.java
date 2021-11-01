package com.car.castel.BookingService.service.impl;

import com.car.castel.BookingService.entity.Garage;
import com.car.castel.BookingService.repository.GarageRepository;
import com.car.castel.BookingService.service.CRUDServices;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GarageService implements CRUDServices<Garage> {

    @Autowired
    private GarageRepository garageRepository;
    @Override
    public Garage create(Garage garage) {
        return garageRepository.save(garage);
    }

    @Override
    public Garage update(UUID uuid, Garage garage) {
        Optional<Garage> garage1 = garageRepository.findById(uuid);
        if (garage1.isPresent()){
            return  garageRepository.save(garage);
        }else {
            throw new EntityNotFoundException(Garage.class,"id",uuid.toString());
        }
    }

    @Override
    public Garage get(UUID uuid) {
        Optional<Garage> garage1 = garageRepository.findById(uuid);
        if (garage1.isPresent()){
            return  garage1.get();
        }else {
            throw new EntityNotFoundException(Garage.class,"id",uuid.toString());
        }
    }

    @Override
    public void delete(UUID uuid) {
        Optional<Garage> garage1 = garageRepository.findById(uuid);
        if (garage1.isPresent()){
            garageRepository.delete(garage1.get());
        }else {
            throw new EntityNotFoundException(Garage.class,"id",uuid.toString());
        }
    }

    @Override
    public List<Garage> createAll(List<Garage> list) {
        return list.stream().parallel().map(this::create).collect(Collectors.toList());
    }

    @Override
    public List<Garage> getAll() {
        return garageRepository.findAll();
    }
}
