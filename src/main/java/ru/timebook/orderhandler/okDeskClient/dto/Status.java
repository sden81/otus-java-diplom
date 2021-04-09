package ru.timebook.orderhandler.okDeskClient.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Status {
    private StatusCodes code;
    private String name;
}
