package ru.timebook.orderhandler.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.timebook.orderhandler.healthcheck.HealthcheckService;
import ru.timebook.orderhandler.healthcheck.dto.ReadinessHealthcheck;

@RestController
public class HealthcheckController {
    private final HealthcheckService healthcheckService;

    public HealthcheckController(HealthcheckService healthcheckService) {
        this.healthcheckService = healthcheckService;
    }

    @GetMapping("/health/readiness")
    public ReadinessHealthcheck readinessHealthcheck() {
        return healthcheckService.getReadinessHealthcheck();
    }

    @GetMapping("/health/liveness")
    public ResponseEntity livenessHealthcheck() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
