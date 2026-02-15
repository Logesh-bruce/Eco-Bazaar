package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSellerDTO {
    private Long sellerId;
    private String sellerName;
    private Integer productsSold;
    private Double revenue;
    private Double averageCarbonImpact;
}
