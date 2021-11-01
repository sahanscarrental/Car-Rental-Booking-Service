package com.car.castel.BookingService.service.impl;

import com.car.castel.BookingService.entity.Balance;
import com.car.castel.BookingService.repository.BalanceRepository;
import com.car.castel.BookingService.service.CRUDServices;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BalanceService implements CRUDServices<Balance> {

    @Autowired
    private BalanceRepository balanceRepository;

    @Override
    public Balance create(Balance balance) {
        return balanceRepository.save(balance);
    }

    @Override
    public Balance update(UUID uuid, Balance balance) {
        Optional<Balance> balance1 = balanceRepository.findById(uuid);
        if (balance1.isPresent()){
            return balanceRepository.save(balance);
        }else {
            throw new EntityNotFoundException(Balance.class,"id",uuid.toString());
        }
    }

    @Override
    public Balance get(UUID uuid) {
        Optional<Balance> balance1 = balanceRepository.findById(uuid);
        if (balance1.isPresent()){
            return balance1.get();
        }else {
            throw new EntityNotFoundException(Balance.class,"id",uuid.toString());
        }
    }

    @Override
    public void delete(UUID uuid) {
        Optional<Balance> balance1 = balanceRepository.findById(uuid);
        if (balance1.isPresent()){
            balanceRepository.delete(balance1.get());
        }else {
            throw new EntityNotFoundException(Balance.class,"id",uuid.toString());
        }
    }

    @Override
    public List<Balance> createAll(List<Balance> list) {
        return list.stream().parallel().map(this::create).collect(Collectors.toList());
    }

    @Override
    public List<Balance> getAll() {
        return balanceRepository.findAll();
    }
}
