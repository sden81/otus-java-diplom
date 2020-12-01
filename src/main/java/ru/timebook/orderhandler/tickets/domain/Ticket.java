package ru.timebook.orderhandler.tickets.domain;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import ru.timebook.orderhandler.okDeskClient.dto.Author;
import ru.timebook.orderhandler.okDeskClient.dto.UserComment;
import ru.timebook.orderhandler.okDeskClient.dto.Status;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class Ticket {
    long id;
    String title;
    String description;
    Author author;
    LocalDateTime createdAt;
    LocalDateTime completedAt;
    LocalDateTime updatedAt;
    @Singular("addComment")
    List<UserComment> userComments;
    Status status;

    public static String processedTicketCommentText = "Данные из заявки перенесены в таблицу";
}
