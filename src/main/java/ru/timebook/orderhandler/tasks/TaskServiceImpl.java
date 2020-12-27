package ru.timebook.orderhandler.tasks;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.timebook.orderhandler.OrderHandler;
import ru.timebook.orderhandler.tickets.TicketService;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TaskServiceImpl implements TaskService {
    private final TicketService ticketService;

    @Value("${schedulingInterval:0}")
    private Long schedulingInterval;

    @Value("${generateTokenOnly:false}")
    private boolean generateTokenOnly;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @Value("${processIssueIdsOnly:}#{T(java.util.Collections).emptySet()}")
    private Set<Long> processIssueIdsOnly;

    private final Logger logger = LoggerFactory.getLogger(OrderHandler.class);

    public TaskServiceImpl(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void runTask() {
        Runnable task = getTask();

        if (schedulingInterval > 0) {
            scheduledExecutorService.scheduleAtFixedRate(task, 0, schedulingInterval, TimeUnit.SECONDS);
        } else {
            task.run();
        }
    }

    private Runnable getTask() {
        //for google sheet Token generating
        if (generateTokenOnly) {
            return () -> {
                logger.info("Generating google sheet access Token in 'Token' directory");
            };
        }

        return () -> {
            try {
                synchronized (this) {
                    var needProcessedTickets = processIssueIdsOnly.isEmpty() ?
                            ticketService.getNeedProcessedTickets() :
                            ticketService.getNeedProcessedTickets(processIssueIdsOnly);

                    logger.info("Find {} need processed tickets", needProcessedTickets.size());
                    needProcessedTickets.forEach(ticketService::processTicket);
                }
            } catch (Exception ex) {
                logger.error("Something wrong", ex);
                shutdown();
                System.exit(1);
            }
        };
    }

    @SneakyThrows
    public void shutdown() {
        if (scheduledExecutorService == null) {
            return;
        }

        logger.info("Shutdown running tasks");
        scheduledExecutorService.shutdown();

        Thread.sleep(2000);

        if (!scheduledExecutorService.isTerminated()) {
            Thread.sleep(5000);
            if (!scheduledExecutorService.isTerminated()) {
                logger.info("Forced shutdown running tasks");
                scheduledExecutorService.shutdownNow();
                Thread.sleep(2000);
            }
        }
    }

    public Boolean isSingleTask() {
        return schedulingInterval == 0;
    }
}
