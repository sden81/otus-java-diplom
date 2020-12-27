package ru.timebook.orderhandler.tickets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.timebook.orderhandler.OrderHandler;
import ru.timebook.orderhandler.spreadsheet.SpreadsheetRepository;
import ru.timebook.orderhandler.spreadsheet.exceptions.OrderRecordAlreadyExistException;
import ru.timebook.orderhandler.tickets.domain.Order;
import ru.timebook.orderhandler.tickets.domain.Ticket;
import ru.timebook.orderhandler.tickets.exceptions.TicketException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final SpreadsheetRepository spreadsheetRepository;
    private final TicketParser ticketParser;

    private final List<Long> excludedOkDeskIssueIds = Collections.synchronizedList(new ArrayList<>());

    private final Logger logger = LoggerFactory.getLogger(OrderHandler.class);

    @Value("${okDesk.processed_comment_marker}")
    private String processedCommentMarker;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            SpreadsheetRepository spreadsheetRepository,
            TicketParser ticketParser) {
        this.ticketRepository = ticketRepository;
        this.spreadsheetRepository = spreadsheetRepository;
        this.ticketParser = ticketParser;
    }

    @Override
    public Set<Ticket> getNeedProcessedTickets() {
        var issueIds = removeProcessedIds(ticketRepository.getNeedProcessTicketIds());
        var tickets = ticketRepository.getTickets(issueIds);

        return Set.copyOf(tickets);
    }

    @Override
    public Set<Ticket> getNeedProcessedTickets(Set<Long> issueIdsFilter) {
        var issueIds = removeProcessedIds(ticketRepository.getNeedProcessTicketIds());
        var filteredIds = issueIds.stream()
                .filter(issueId -> issueIdsFilter.contains(issueId))
                .collect(Collectors.toList());
        var tickets = ticketRepository.getTickets(filteredIds);

        return Set.copyOf(tickets);
    }

    private List<Long> removeProcessedIds(List<Long> issueIds) {
        return issueIds.stream().filter(issueId -> !excludedOkDeskIssueIds.contains(issueId)).collect(Collectors.toList());
    }

    @Override
    public void processTicket(@NonNull Ticket ticket) {
        if (!isNeedProcessTicket(ticket)) {
            addExcludedOkDeskIssueId(ticket.getId());
            return;
        }

        logger.info("Start process ticket: {}", ticket.getId());
        Order order = parseTicket(ticket);
        String spreadsheetTitle = order.getSpreadsheetTitle();

        if (!spreadsheetRepository.getSheetsTitles().containsValue(spreadsheetTitle)) {
            spreadsheetRepository.createSheet(spreadsheetTitle);
        }

        try {
            if (spreadsheetRepository.insertOrderData(spreadsheetTitle, order)) {
                addExcludedOkDeskIssueId(ticket.getId());
                ticketRepository.markTicketAsProcessed(ticket, processedCommentMarker);
                logger.info("Order '{}' added to spreadsheet at sheet '{}'", order.getOrderId(), spreadsheetTitle);
            } else {
                throw new TicketException("Can't insert order to spreadsheet");
            }
        } catch (OrderRecordAlreadyExistException ex) {
            logger.info("Order '{}' already exist in spreadsheet", order.getOrderId());
            return;
        }
    }

    @Override
    public Order parseTicket(@NonNull Ticket ticket) {
        return ticketParser.parseTicket(ticket);
    }

    @Override
    public boolean isNeedProcessTicket(@NonNull Ticket ticket) {
        if (ticket.getUserComments().isEmpty()) {
            return true;
        }

        return ticket.getUserComments().stream().noneMatch(comment -> comment.getContent().contains(processedCommentMarker));
    }

    @Override
    public void addExcludedOkDeskIssueId(Long id){
        excludedOkDeskIssueIds.add(id);
    }
}
