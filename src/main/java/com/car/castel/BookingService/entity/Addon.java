package com.car.castel.BookingService.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Addon {

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
    private int totalCount = 0;

    @Column
    private int bookedCount = 0;

    @Column
    private int availableCount = 0;

    @Column(nullable = true)
    private UUID imageId;


    @JsonIgnore
    @ManyToMany(mappedBy = "addons")
    private List<BookingRecord> bookings = new ArrayList<>();

    @Override
    public String toString() {
        return "Addon{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", name='" + name + '\'' +
                ", imageId=" + imageId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Addon addon = (Addon) o;
        return name.equals(addon.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
