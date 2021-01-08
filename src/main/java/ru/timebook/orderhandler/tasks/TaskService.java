package ru.timebook.orderhandler.tasks;

public interface TaskService {
    void runTask();
    void shutdown();
    boolean waitShutdown();
}
