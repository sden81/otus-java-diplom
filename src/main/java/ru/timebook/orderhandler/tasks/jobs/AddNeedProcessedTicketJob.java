package ru.timebook.orderhandler.tasks.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.timebook.orderhandler.tickets.TicketService;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class AddNeedProcessedTicketJob implements Runnable{
    private final BlockingQueue<Ticket> needProcessTicketsQueue;
    private final Set<Long> processIssueIdsOnly;
    private final TicketService ticketService;
    private final Logger logger = LoggerFactory.getLogger(AddNeedProcessedTicketJob.class);

    public AddNeedProcessedTicketJob(BlockingQueue<Ticket> needProcessTicketsQueue, Set<Long> processIssueIdsOnly, TicketService ticketService) {
        this.needProcessTicketsQueue = needProcessTicketsQueue;
        this.processIssueIdsOnly = processIssueIdsOnly;
        this.ticketService = ticketService;
    }

    @Override
    public void run() {
        var needProcessedTickets = processIssueIdsOnly.isEmpty() ?
                ticketService.getNeedProcessedTickets() :
                ticketService.getNeedProcessedTickets(processIssueIdsOnly);
        logger.info("Find {} need processed tickets", needProcessedTickets.size());
        needProcessedTickets.forEach(ticket -> {
            try {
                needProcessTicketsQueue.put(ticket);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
