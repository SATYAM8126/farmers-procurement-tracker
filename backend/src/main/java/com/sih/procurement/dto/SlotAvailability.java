package com.sih.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotAvailability {
    private Integer hour;
    private String label;
    private long booked;
    private int capacity;
    private boolean available;
}
