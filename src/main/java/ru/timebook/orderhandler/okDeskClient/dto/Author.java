package ru.timebook.orderhandler.okDeskClient.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Author {
    private final long id;
    private String name;
    private String type;
}
