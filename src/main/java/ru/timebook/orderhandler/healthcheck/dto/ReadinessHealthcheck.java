package ru.timebook.orderhandler.healthcheck.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

@Data
@Builder
public class ReadinessHealthcheck {
    private final Integer readinessHealthcheckStatus;
    @Singular("addItem")
    private final Set<Item> items;
}
