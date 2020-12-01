package ru.timebook.orderhandler.tasks;

import java.util.Optional;
import java.util.Set;

public interface RunOptions {
    void parseArguments(String[] args);

    Optional<Set<Long>> getProcessIssueIdsOnly();

    Optional<Integer> getSchedulingInterval();

    boolean isGenerateTokenOnly();
}
