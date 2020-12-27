package ru.timebook.orderhandler.okDeskClient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class UserComment {
    private final long id;

    @JsonProperty("public")
    private final boolean isPublic;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime published_at;

    private final Author author;
}
