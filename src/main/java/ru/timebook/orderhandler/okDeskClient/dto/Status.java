package ru.timebook.orderhandler.okDeskClient.dto;

import lombok.Value;

@Value
public class Status {
    StatusCodes code;
    String name;
}
