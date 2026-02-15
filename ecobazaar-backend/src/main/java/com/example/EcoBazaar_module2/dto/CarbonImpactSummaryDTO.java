package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonImpactSummaryDTO {
    private Double totalCarbonFootprint;
    private Double totalCarbonSaved;
    private Double averageCarbonPerOrder;
    private Integer highImpactProducts; // > 5kg
    private Integer lowImpactProducts;  // < 2kg
    private Map<String, Double> carbonByCategory;
}
