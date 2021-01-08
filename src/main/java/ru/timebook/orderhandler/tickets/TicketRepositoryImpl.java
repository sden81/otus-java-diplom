package ru.timebook.orderhandler.tickets;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.timebook.orderhandler.okDeskClient.IssueListFilter;
import ru.timebook.orderhandler.okDeskClient.OkDeskRepository;
import ru.timebook.orderhandler.okDeskClient.dto.Issue;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TicketRepositoryImpl implements TicketRepository {
    private final OkDeskRepository okDeskRepository;

    private final IssueListFilter listFilter;

    public TicketRepositoryImpl(OkDeskRepository okDeskRepository, IssueListFilter listFilter) {
        this.okDeskRepository = okDeskRepository;
        this.listFilter = listFilter;
    }

    @Override
    public List<Long> getNeedProcessTicketIds() {
        return okDeskRepository.getIssueIdsList(listFilter);
    }

    @Override
    public List<Ticket> getTickets(@NonNull List<Long> ticketIds) {
        List<Ticket> ticketsList = new ArrayList<>();
        ticketIds.stream().forEach(id -> {
                    var ticket = getTicket(id);
                    if (ticket.isPresent()) {
                        ticketsList.add(ticket.get());
                    }
                }
        );

        return ticketsList;
    }

    @Override
    public Optional<Ticket> getTicket(Long id) {
        Optional<Issue> issue = okDeskRepository.getIssue(id);
        if (issue.isEmpty()) {
            return Optional.empty();
        }

        var receivedIssue = issue.get();
        var comments = okDeskRepository.getComments(id);
        var ticketBuilder = Ticket.builder()
                .id(receivedIssue.getId())
                .title(receivedIssue.getTitle())
                .description(receivedIssue.getDescription())
                .author(receivedIssue.getAuthor())
                .createdAt(receivedIssue.getCreatedAt())
                .updatedAt(receivedIssue.getUpdatedAt())
                .completedAt(receivedIssue.getCompletedAt())
                .status(receivedIssue.getStatus());
        if (!comments.isEmpty()) {
            ticketBuilder.userComments(comments);
        }

        return Optional.of(ticketBuilder.build());
    }

    @Override
    public void markTicketAsProcessed(Ticket ticket, String processedComment) {
        okDeskRepository.addComment(ticket.getId(), processedComment);
    }
}
