package ru.timebook.orderhandler.tickets;

import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    List<Long> getNeedProcessTicketIds();

    List<Ticket> getTickets(List<Long> ticketIds);

    List<Long> getExcludedTicketIds();

    Optional<Ticket> getTicket(Long id);

    void addExcludedOkDeskIssueIds(Long excludedOkDeskIssueId);

    void markTicketAsProcessed(Ticket ticket);
}
