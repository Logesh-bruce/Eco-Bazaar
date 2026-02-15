package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponseDTO {
    private String errorCode;
    private String message;
    private long timestamp;

    public ErrorResponseDTO(String errorCode, String message, long timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
    }


}