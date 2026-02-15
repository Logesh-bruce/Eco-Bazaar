package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueBreakdownDTO {
    private Double todayRevenue;
    private Double weekRevenue;
    private Double monthRevenue;
    private Double totalRevenue;
}
