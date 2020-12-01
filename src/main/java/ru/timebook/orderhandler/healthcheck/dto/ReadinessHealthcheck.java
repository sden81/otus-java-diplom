package ru.timebook.orderhandler.healthcheck.dto;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class ReadinessHealthcheck {
    Integer readinessHealthcheckStatus;
    @Singular("addItem")
    Set<Item> items;
}
