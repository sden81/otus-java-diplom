package ru.timebook.orderhandler.tickets.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.Value;
import ru.timebook.orderhandler.okDeskClient.dto.Author;
import ru.timebook.orderhandler.okDeskClient.dto.UserComment;
import ru.timebook.orderhandler.okDeskClient.dto.Status;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Ticket {
    private final long id;
    private String title;
    private String description;
    private final Author author;
    private final LocalDateTime createdAt;
    private final LocalDateTime completedAt;
    private final LocalDateTime updatedAt;
    @Singular("addComment")
    private final List<UserComment> userComments;
    private final Status status;
}
