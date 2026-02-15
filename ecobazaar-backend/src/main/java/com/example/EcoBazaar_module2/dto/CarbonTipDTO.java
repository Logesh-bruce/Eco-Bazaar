package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonTipDTO {
    private String icon;
    private String message;
    private String category;
    private String priority; // HIGH, MEDIUM, LOW
}
