package ru.timebook.orderhandler.healthcheck.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

@Data
@Builder
public class ReadinessHealthCheck {
    private final Integer readinessHealthCheckStatus;
    @Singular("addItem")
    private final Set<Item> items;
}
