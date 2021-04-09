package ru.timebook.orderhandler.healthcheck.dto;

import lombok.Data;

@Data
public class Item {
    private final String name;
    private final Boolean success;
}
