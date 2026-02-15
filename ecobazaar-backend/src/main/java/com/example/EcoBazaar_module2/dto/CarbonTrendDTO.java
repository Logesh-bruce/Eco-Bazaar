package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonTrendDTO {
    private List<String> labels; // Month labels
    private List<Double> carbonData;
    private List<Integer> orderCounts;
}
