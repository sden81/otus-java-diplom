package ru.timebook.orderhandler.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timebook.orderhandler.OrderHandler;
import ru.timebook.orderhandler.tickets.TicketService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TaskServiceImpl implements TaskService{
    @Autowired
    TicketService ticketService;

    Set<Long> processIssueIdsOnly;
    Integer schedulingInterval;

    Logger logger = LoggerFactory.getLogger(OrderHandler.class);

    public void runTask(RunOptions runOptions) {
        processIssueIdsOnly = runOptions.getProcessIssueIdsOnly().orElse(new HashSet<>());
        schedulingInterval = runOptions.getSchedulingInterval().orElse(0);

        Runnable task = getTask(runOptions);
        if (schedulingInterval != null && schedulingInterval > 0) {
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleAtFixedRate(task, 0, schedulingInterval, TimeUnit.SECONDS);
        } else {
            task.run();
            System.exit(0);
        }
    }

    private Runnable getTask(RunOptions runOptions) {
        //for google sheet Token generating
        if (runOptions.isGenerateTokenOnly()){
            return ()->{
                logger.info("Generating google sheet access Token in 'Token' directory");
                System.exit(0);
            };
        }

        return () -> {
            try {
                var needProcessedTickets = processIssueIdsOnly.isEmpty() ?
                        ticketService.getNeedProcessedTickets() :
                        ticketService.getNeedProcessedTickets(processIssueIdsOnly);

                logger.info("Find {} need processed tickets", needProcessedTickets.size());

                needProcessedTickets.forEach((id, ticket) -> {
                    ticketService.processTicket(ticket);
                });
            } catch (Exception ex) {
                logger.error("Something wrong", ex);
                System.exit(1);
            }
        };
    }
}
