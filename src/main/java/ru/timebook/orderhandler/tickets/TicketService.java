package ru.timebook.orderhandler.tickets;

import org.springframework.lang.NonNull;
import ru.timebook.orderhandler.tickets.domain.Order;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.util.Map;
import java.util.Set;

public interface TicketService {
    Set<Ticket> getNeedProcessedTickets();

    Set<Ticket> getNeedProcessedTickets(Set<Long> issueIdsFilter);

    void processTicket(@NonNull Ticket ticket);

    Order parseTicket(@NonNull Ticket ticket);

    boolean isNeedProcessTicket(@NonNull Ticket ticket);

    void addExcludedOkDeskIssueId(Long id);
}
