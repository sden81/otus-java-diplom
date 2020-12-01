package ru.timebook.orderhandler.okDeskClient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class UserComment {
    long id;

    @JsonProperty("public")
    boolean isPublic;

    String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime published_at;

    Author author;
}
