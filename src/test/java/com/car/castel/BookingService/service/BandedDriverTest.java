package com.car.castel.BookingService.service;

import com.car.castel.BookingService.BookingServiceApplication;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.Assert.*;

@SpringBootTest(classes = BookingServiceApplication.class)
public class BandedDriverTest {
    @InjectMocks
    private BandedDriver bandedDriver = new BandedDriver();

    @BeforeAll
    public void setup() {
        bandedDriver.initialize();
    }

    @Test
    public void initialize() {
        assertEquals(Integer.valueOf(0), bandedDriver.getSize());
    }

    @Test
    public void add() {
        bandedDriver.add("GN123","test");
        boolean driverIn = bandedDriver.isIn("GN123");
        assertTrue(driverIn);
        boolean driverIn1 = bandedDriver.isIn("GN1234");
        assertFalse(driverIn1);
    }

    @Test
    public void get() {
        bandedDriver.add("GN123","test");
        String gn123 = bandedDriver.get("GN123");
        assertEquals("test",gn123);
    }

    @Test
    public void isIn() {
        bandedDriver.add("GN123","test");
        boolean driverIn = bandedDriver.isIn("GN123");
        assertTrue(driverIn);
        boolean driverIn1 = bandedDriver.isIn("GN1234");
        assertFalse(driverIn1);
    }
}