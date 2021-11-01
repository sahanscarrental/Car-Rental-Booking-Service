package com.car.castel.BookingService.entity;

import com.car.castel.BookingService.entity.type.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "garage_id")
    private Garage garage;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    @Column
    private Integer seats;

    @Column()
    private Double costPerDay;

    @Column
    private Currency currency;

    @Column
    private GearType gearType;

    @Column
    private FuelType fuelType;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private VehicleCategory vehicleCategory;

    @Column
    private UUID imageId;

    @Column
    private String vehicleNo;

}
