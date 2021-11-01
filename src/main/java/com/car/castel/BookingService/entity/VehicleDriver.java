package com.car.castel.BookingService.entity;

import com.car.castel.BookingService.entity.auth.User;
import com.car.castel.BookingService.entity.type.DriverState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDriver {

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

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private Long nic;


    @Column
    private String licenceNo;

    @Column
    private DriverState driverState;

    @Column
    private UUID imageId;

    @Column
    private UUID licenceFrontImageId;

    @Column(nullable = true)
    private UUID councilTaxImageId;

    @Column(nullable = true)
    private UUID utilityBillId;

    @Column(nullable = true)
    private UUID bankStatementId;

    @Column
    private UUID nicFrontImageId;

    @Column
    private UUID nicBackImageId;

    @Column
    private UUID faceImageId;

    @Email
    @Column
    private String email;

    @Column
    private String phoneNo;

    @Column()
    private Boolean emailVerified;

    @Column
    private Boolean phoneNoVerified;

    @Column()
    @DateTimeFormat(pattern = "yyyy-MM-ddTH:ms")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dob;

    @Column
    private Long successBookings;

    @Column
    private String regNo;

    @Column
    private String password;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public void incrementBookingCount(){
        this.successBookings = this.successBookings + 1;
    }

}
