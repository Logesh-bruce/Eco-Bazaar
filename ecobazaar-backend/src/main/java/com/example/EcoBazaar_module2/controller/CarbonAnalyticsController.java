package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.service.CarbonAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carbon")
public class CarbonAnalyticsController {

    @Autowired
    private CarbonAnalyticsService carbonAnalyticsService;

    @GetMapping("/report/{userId}")
    public ResponseEntity<Map<String, Object>> getUserCarbonReport(@PathVariable Long userId) {
        Map<String, Object> report = carbonAnalyticsService.getUserCarbonReport(userId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/admin/summary")
    public ResponseEntity<Map<String, Object>> getPlatformSummary() {
        Map<String, Object> summary = carbonAnalyticsService.getPlatformCarbonSummary();
        return ResponseEntity.ok(summary);
    }
}