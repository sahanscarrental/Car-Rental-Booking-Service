package com.car.castel.BookingService.entity;


import com.car.castel.BookingService.entity.type.BookingRecordState;
import com.car.castel.BookingService.entity.type.Currency;
import com.car.castel.BookingService.entity.type.PaymentState;
import com.car.castel.BookingService.entity.type.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRecord {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    @OneToOne(cascade = CascadeType.ALL)
    private Vehicle vehicle;

    @OneToOne(cascade = CascadeType.ALL)
    private VehicleDriver vehicleDriver;

    @Column()
    @Temporal(TemporalType.TIMESTAMP)
    private Date pickUpTime;

    @Column()
    @Temporal(TemporalType.TIMESTAMP)
    private Date dropTime;

    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date extendedDropTime;

    @Column()
    private Double cost;

    @Column
    private Currency currency;

    @Column
    private PaymentState paymentState;

    @Column
    private PaymentType paymentType;

    @Column
    private BookingRecordState bookingRecordState;

    @ManyToMany
    @JoinTable(
            name="booking_record_addons",
            joinColumns = @JoinColumn(name = "bookings_id"),
            inverseJoinColumns = @JoinColumn(name = "addons_id")
    )
    private List<Addon> addons = new ArrayList<>();

    @Column
    private Boolean returnAtNight;

    public void addAddOn(Addon addon) {
        this.addons.add(addon);
    }


}
