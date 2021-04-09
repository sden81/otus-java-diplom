package ru.timebook.orderhandler.tasks;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.timebook.orderhandler.OrderHandler;
import ru.timebook.orderhandler.tasks.jobs.AddNeedProcessedTicketJob;
import ru.timebook.orderhandler.tasks.jobs.ProcessTicketJob;
import ru.timebook.orderhandler.tasks.jobs.SingleProcessTicketJob;
import ru.timebook.orderhandler.tasks.jobs.TokenGenerationJob;
import ru.timebook.orderhandler.tickets.TicketService;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class TaskServiceImpl implements TaskService {
    private final TicketService ticketService;

    private final Long schedulingInterval;

    private final boolean generateTokenOnly;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    private final Set<Long> processIssueIdsOnly;

    private final BlockingQueue<Ticket> needProcessTicketsQueue = new ArrayBlockingQueue<>(100);

    private final Logger logger = LoggerFactory.getLogger(OrderHandler.class);

    public TaskServiceImpl(
            TicketService ticketService,
            @Value("${schedulingInterval:0}") Long schedulingInterval,
            @Value("${generateTokenOnly:false}") boolean generateTokenOnly,
            @Value("${processIssueIdsOnly:}#{T(java.util.Collections).emptySet()}") Set<Long> processIssueIdsOnly
    ) {
        this.ticketService = ticketService;
        this.schedulingInterval = schedulingInterval;
        this.generateTokenOnly = generateTokenOnly;
        this.processIssueIdsOnly = processIssueIdsOnly;
    }

    public void runTask() {
        var jobs = getJobs();

        jobs.forEach((job) -> {
            if (job instanceof AddNeedProcessedTicketJob) {
                scheduledExecutorService.scheduleAtFixedRate(job, 0, schedulingInterval, TimeUnit.SECONDS);
            } else {
                scheduledExecutorService.execute(job);
            }
        });
    }

    private Set<Runnable> getJobs() {
        var jobs = new HashSet<Runnable>();

        if (generateTokenOnly) {
            jobs.add(new TokenGenerationJob(this));
            return jobs;
        }

        if (isSingleTask()) {
            jobs.add(new SingleProcessTicketJob(
                    processIssueIdsOnly,
                    ticketService,
                    this
            ));
            return jobs;
        }

        jobs.add(new AddNeedProcessedTicketJob(
                needProcessTicketsQueue,
                processIssueIdsOnly,
                ticketService
        ));
        jobs.add(new ProcessTicketJob(
                needProcessTicketsQueue,
                ticketService
        ));

        return jobs;
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
        return schedulingInterval == 0 || generateTokenOnly;
    }

    @Override
    @SneakyThrows
    public boolean waitShutdown() {
        return scheduledExecutorService.awaitTermination(20, TimeUnit.SECONDS);
    }
}
