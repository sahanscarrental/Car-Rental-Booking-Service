package com.car.castel.BookingService.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class FakeClaim {
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
}
