package ru.timebook.orderhandler.tasks.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.timebook.orderhandler.tasks.TaskService;

public class TokenGenerationJob implements Runnable{
    private final TaskService taskService;
    private final Logger logger = LoggerFactory.getLogger(TokenGenerationJob.class);

    public TokenGenerationJob(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void run() {
        logger.info("Generating google sheet access Token in 'Token' directory");
        taskService.shutdown();
    }
}
