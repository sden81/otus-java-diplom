package ru.timebook.orderhandler.healthcheck.dto;

import lombok.Value;

@Value
public class Item {
    String name;
    Boolean success;
}
