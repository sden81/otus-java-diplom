package ru.timebook.orderhandler.tasks;

public interface TaskService {
    void runTask(RunOptions runOptions);
    void shutdown();
}
