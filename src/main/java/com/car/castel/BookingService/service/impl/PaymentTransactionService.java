package com.car.castel.BookingService.service.impl;

import com.car.castel.BookingService.entity.PaymentTransaction;
import com.car.castel.BookingService.repository.PaymentTransactionRepository;
import com.car.castel.BookingService.service.CRUDServices;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentTransactionService implements CRUDServices<PaymentTransaction> {

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    @Override
    public PaymentTransaction create(PaymentTransaction paymentTransaction) {
        return paymentTransactionRepository.save(paymentTransaction);
    }

    @Override
    public PaymentTransaction update(UUID uuid, PaymentTransaction paymentTransaction) {
        Optional<PaymentTransaction> paymentTransaction1 = paymentTransactionRepository.findById(uuid);
        if (paymentTransaction1.isPresent()){
            return  paymentTransactionRepository.save(paymentTransaction);
        }else {
            throw new EntityNotFoundException(PaymentTransaction.class,"id",uuid.toString());
        }
    }

    @Override
    public PaymentTransaction get(UUID uuid) {
        Optional<PaymentTransaction> paymentTransaction1 = paymentTransactionRepository.findById(uuid);
        if (paymentTransaction1.isPresent()){
            return  paymentTransaction1.get();
        }else {
            throw new EntityNotFoundException(PaymentTransaction.class,"id",uuid.toString());
        }
    }

    @Override
    public void delete(UUID uuid) {
        Optional<PaymentTransaction> paymentTransaction1 = paymentTransactionRepository.findById(uuid);
        if (paymentTransaction1.isPresent()){
           paymentTransactionRepository.delete(paymentTransaction1.get());
        }else {
            throw new EntityNotFoundException(PaymentTransaction.class,"id",uuid.toString());
        }
    }

    @Override
    public List<PaymentTransaction> createAll(List<PaymentTransaction> list) {
        return list.stream().parallel().map(this::create).collect(Collectors.toList());
    }

    @Override
    public List<PaymentTransaction> getAll() {
        return paymentTransactionRepository.findAll();
    }
}
