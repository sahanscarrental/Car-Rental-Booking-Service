package com.car.castel.BookingService.service;

import java.util.List;
import java.util.UUID;


public interface CRUDServices<T> {
    T create(T t);
    T update(UUID uuid, T t);
    T get(UUID uuid);
    void delete(UUID uuid);
    List<T> createAll(List<T> list);
    List<T> getAll();
}
