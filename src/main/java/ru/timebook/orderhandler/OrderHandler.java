package ru.timebook.orderhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import ru.timebook.orderhandler.tasks.RunOptionsImpl;
import ru.timebook.orderhandler.tasks.TaskServiceImpl;

@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
public class OrderHandler {
        public static void main(String[] args) {
            ApplicationContext context = SpringApplication.run(OrderHandler.class, args);

            var runOptions = context.getBean(RunOptionsImpl.class);
            runOptions.parseArguments(args);

            var taskService = context.getBean(TaskServiceImpl.class);
            taskService.runTask(runOptions);
        }
    }
