package ru.timebook.orderhandler.tickets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.timebook.orderhandler.OrderHandler;
import ru.timebook.orderhandler.spreadsheet.exceptions.OrderRecordAlreadyExistException;
import ru.timebook.orderhandler.spreadsheet.SpreadsheetRepositoryImpl;
import ru.timebook.orderhandler.tickets.exceptions.TicketException;
import ru.timebook.orderhandler.tickets.domain.Order;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    SpreadsheetRepositoryImpl spreadsheetRepository;

    @Autowired
    TicketParser ticketParser;

    Logger logger = LoggerFactory.getLogger(OrderHandler.class);

    @Override
    public Map<Long, Ticket> getNeedProcessedTickets() {
        var ticketIds = ticketRepository.getNeedProcessTicketIds();
        var tickets = ticketRepository.getTickets(ticketIds);

        return tickets.stream().collect(Collectors.toMap(Ticket::getId, Function.identity()));
    }

    @Override
    public Map<Long, Ticket> getNeedProcessedTickets(Set<Long> issueIdsFilter) {
        var ticketIds = ticketRepository.getNeedProcessTicketIds();
        var filteredIds = ticketIds.stream()
                .filter(ticketId -> issueIdsFilter.contains(ticketId))
                .collect(Collectors.toList());
        var tickets = ticketRepository.getTickets(filteredIds);

        return tickets.stream().collect(Collectors.toMap(Ticket::getId, Function.identity()));
    }

    @Override
    public void processTicket(@NonNull Ticket ticket) {
        if (!isNeedProcessTicket(ticket)) {
            ticketRepository.addExcludedOkDeskIssueIds(ticket.getId());
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
                ticketRepository.addExcludedOkDeskIssueIds(ticket.getId());
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

        return ticket.getUserComments().stream().noneMatch(comment -> comment.getContent().contains(Ticket.processedTicketCommentText));
    }
}
