package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDTO {
    private String id;
    private String name;
    private String description;
    private String icon;
    private boolean unlocked;
    private Integer requiredValue;
    private Integer currentValue;
}
