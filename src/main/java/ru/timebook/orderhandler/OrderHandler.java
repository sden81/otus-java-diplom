package ru.timebook.orderhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import ru.timebook.orderhandler.tasks.TaskServiceImpl;

@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
public class OrderHandler {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(OrderHandler.class, args);

        try {
            var taskService = context.getBean(TaskServiceImpl.class);
            taskService.runTask();
            if (taskService.isSingleTask()) {
                if (taskService.waitShutdown()){
                    System.out.println("All jobs finished");
                    System.exit(0);
                } else {
                    System.out.println("Some jobs not finished");
                    System.exit(1);
                }
            }
        } catch (Exception ex) {
            System.exit(1);
        }
    }
}
