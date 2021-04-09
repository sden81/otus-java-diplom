package ru.timebook.orderhandler.tasks.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.timebook.orderhandler.tasks.TaskService;
import ru.timebook.orderhandler.tasks.TaskServiceException;
import ru.timebook.orderhandler.tickets.TicketService;

import java.util.Set;

public class SingleProcessTicketJob implements Runnable{
    private final Set<Long> processIssueIdsOnly;
    private final TicketService ticketService;
    private final Logger logger = LoggerFactory.getLogger(SingleProcessTicketJob.class);
    private final TaskService taskService;

    public SingleProcessTicketJob(Set<Long> processIssueIdsOnly, TicketService ticketService, TaskService taskService) {
        this.processIssueIdsOnly = processIssueIdsOnly;
        this.ticketService = ticketService;
        this.taskService = taskService;
    }

    @Override
    public void run() {
        try {
            var needProcessedTickets = processIssueIdsOnly.isEmpty() ?
                    ticketService.getNeedProcessedTickets() :
                    ticketService.getNeedProcessedTickets(processIssueIdsOnly);

            logger.info("Find {} need processed tickets", needProcessedTickets.size());
            needProcessedTickets.forEach(ticketService::processTicket);
        } catch (Exception ex) {
            logger.error("Something wrong", ex);
            taskService.shutdown();
            throw new TaskServiceException("Task error", ex);
        } finally {
            taskService.shutdown();
        }
    }
}
