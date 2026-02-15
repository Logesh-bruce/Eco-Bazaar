package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingVerificationDTO {
    private Long productId;
    private String productName;
    private Long sellerId;
    private String sellerName;
    private Double carbonFootprint;
    private LocalDateTime submittedAt;
}
