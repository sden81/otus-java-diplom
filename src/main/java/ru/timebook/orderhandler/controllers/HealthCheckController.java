package ru.timebook.orderhandler.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.timebook.orderhandler.healthcheck.HealthCheckService;
import ru.timebook.orderhandler.healthcheck.dto.ReadinessHealthCheck;

@RestController
public class HealthCheckController {
    private final HealthCheckService healthcheckService;

    public HealthCheckController(HealthCheckService healthcheckService) {
        this.healthcheckService = healthcheckService;
    }

    @GetMapping("/health/readiness")
    public ReadinessHealthCheck readinessHealthcheck() {
        return healthcheckService.getReadinessHealthCheck();
    }

    @GetMapping("/health/liveness")
    public ResponseEntity<?> livenessHealthCheck() {
        return ResponseEntity.ok().build();
    }
}
