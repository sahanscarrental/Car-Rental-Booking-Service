package com.car.castel.BookingService.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class BandedDriver {
    private HashMap<String, String> licenceNos = new HashMap<>();

    public void initialize() {
        this.licenceNos = new HashMap<>();
    }

    public Integer getSize(){
        return this.licenceNos.size();
    }
    public void add(String licenceNo, String values){
        this.licenceNos.put(licenceNo, values);
    }
    public String get(String licenceNo){
        return this.licenceNos.get(licenceNo);
    }
    public boolean isIn(String licenceNo){
        return this.licenceNos.containsKey(licenceNo);
    }

    @Override
    public String toString() {
        return "licenceNos=" + licenceNos;
    }
}
