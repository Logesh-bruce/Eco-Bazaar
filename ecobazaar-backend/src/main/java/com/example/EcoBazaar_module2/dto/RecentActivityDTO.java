package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    private String actorName;
    private String action;
    private String entityType;
    private Long entityId;
    private LocalDateTime timestamp;
    private String metadata;
}
