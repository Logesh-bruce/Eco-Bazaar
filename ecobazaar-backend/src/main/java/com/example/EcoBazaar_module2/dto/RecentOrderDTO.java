package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderDTO {
    private Long orderId;
    private LocalDateTime orderDate;
    private Integer itemCount;
    private Double totalAmount;
    private Double totalCarbon;
    private String status;
}
