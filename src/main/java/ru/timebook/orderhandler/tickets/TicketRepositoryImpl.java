package ru.timebook.orderhandler.tickets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.timebook.orderhandler.okDeskClient.IssueListFilter;
import ru.timebook.orderhandler.okDeskClient.OkDeskRepository;
import ru.timebook.orderhandler.okDeskClient.dto.Issue;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TicketRepositoryImpl implements TicketRepository {
    @Autowired
    private OkDeskRepository okDeskRepository;

    @Autowired
    private IssueListFilter listFilter;

    private List<Long> excludedOkDeskIssueIds = new ArrayList<>();

    //use for testing
    private List<Long> includedOnlyOkDeskIssueIds = new ArrayList<>();

    @Override
    public List<Long> getNeedProcessTicketIds() {
        List<Long> okDeskIssueIdsList = okDeskRepository.getIssuesList(listFilter);

        if (!getIncludedOnlyOkDeskIssueIds().isEmpty()){
            return okDeskIssueIdsList.stream()
                    .filter(id -> getIncludedOnlyOkDeskIssueIds().contains(id))
                    .collect(Collectors.toList()
                    );
        }

        return okDeskIssueIdsList.stream()
                .filter(id -> !getExcludedTicketIds().contains(id))
                .collect(Collectors.toList()
                );
    }

    @Override
    public void addExcludedOkDeskIssueIds(Long issueId) {
        excludedOkDeskIssueIds.add(issueId);
    }

    @Override
    public List<Long> getExcludedTicketIds() {
        return excludedOkDeskIssueIds;
    }

    public void addIncludedOnlyOkDeskIssueIds(Long issueId){
        includedOnlyOkDeskIssueIds.add(issueId);
    }

    public List<Long> getIncludedOnlyOkDeskIssueIds(){
        return includedOnlyOkDeskIssueIds;
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
    public void markTicketAsProcessed(Ticket ticket) {
        okDeskRepository.addComment(ticket.getId(), Ticket.processedTicketCommentText);
    }
}
