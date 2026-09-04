package com.sih.procurement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "centres")
@Data
@NoArgsConstructor
public class Centre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    private String operatingHours;

    // Covers the "lack of information regarding procurement schedules" pain point from the problem statement
    private String currentCommodity;      // e.g. "Paddy (Dhaan)"
    private Double mspRatePerQuintal;     // e.g. 2300.0
    private String scheduleInfo;          // e.g. "Kharif procurement: 1 Oct - 31 Dec 2026"

    public Centre(String name, String code, String operatingHours) {
        this.name = name;
        this.code = code;
        this.operatingHours = operatingHours;
    }
}
