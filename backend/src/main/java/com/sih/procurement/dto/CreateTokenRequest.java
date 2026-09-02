package com.sih.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTokenRequest {

    @NotBlank
    private String farmerName;

    private Double quantityQuintal;
}
