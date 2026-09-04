package com.sih.procurement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "slot_bookings")
@Data
@NoArgsConstructor
public class SlotBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centre_id", nullable = false)
    @JsonIgnore
    private Centre centre;

    @Column(nullable = false)
    private String farmerName;

    private String mobileNumber;

    @Column(nullable = false)
    private LocalDate bookingDate;

    // Hour of day the slot starts, e.g. 8 means the 8:00-9:00 AM slot
    @Column(nullable = false)
    private Integer slotHour;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
