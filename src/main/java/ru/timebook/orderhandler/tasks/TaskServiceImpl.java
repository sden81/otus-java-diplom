package ru.timebook.orderhandler.tasks;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.timebook.orderhandler.OrderHandler;
import ru.timebook.orderhandler.tickets.TicketService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TaskServiceImpl implements TaskService {
    TicketService ticketService;

    ScheduledExecutorService scheduledExecutorService;

    Set<Long> processIssueIdsOnly;
    Integer schedulingInterval;
    Boolean singleTask;

    Logger logger = LoggerFactory.getLogger(OrderHandler.class);

    public TaskServiceImpl(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void runTask(RunOptions runOptions) {
        processIssueIdsOnly = runOptions.getProcessIssueIdsOnly().orElse(new HashSet<>());
        schedulingInterval = runOptions.getSchedulingInterval().orElse(0);

        Runnable task = getTask(runOptions);
        singleTask = true;
        if (schedulingInterval != null && schedulingInterval > 0) {
            singleTask = false;
            createScheduler().scheduleAtFixedRate(task, 0, schedulingInterval, TimeUnit.SECONDS);
        } else {
            task.run();
        }
    }

    private Runnable getTask(RunOptions runOptions) {
        //for google sheet Token generating
        if (runOptions.isGenerateTokenOnly()) {
            return () -> {
                logger.info("Generating google sheet access Token in 'Token' directory");
            };
        }

        return () -> {
            try {
                var needProcessedTickets = processIssueIdsOnly.isEmpty() ?
                        ticketService.getNeedProcessedTickets() :
                        ticketService.getNeedProcessedTickets(processIssueIdsOnly);

                logger.info("Find {} need processed tickets", needProcessedTickets.size());

                needProcessedTickets.forEach(ticketService::processTicket);
            } catch (Exception ex) {
                logger.error("Something wrong", ex);
                shutdown();
                System.exit(1);
            }
        };
    }

    @SneakyThrows
    public void shutdown() {
        if (scheduledExecutorService == null){
            return;
        }

        logger.info("Shutdown running tasks");
        scheduledExecutorService.shutdown();

        Thread.sleep(2000);

        if (!scheduledExecutorService.isTerminated()) {
            Thread.sleep(5000);
            if (!scheduledExecutorService.isTerminated()){
                logger.info("Forced shutdown running tasks");
                scheduledExecutorService.shutdownNow();
                Thread.sleep(2000);
            }
        }
    }

    private ScheduledExecutorService createScheduler() {
        return scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    public Boolean isSingleTask() {
        return singleTask;
    }
}
