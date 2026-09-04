package com.sih.procurement.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookSlotRequest {
    private String farmerName;
    private String mobileNumber;
    private LocalDate date;
    private Integer hour;
}
