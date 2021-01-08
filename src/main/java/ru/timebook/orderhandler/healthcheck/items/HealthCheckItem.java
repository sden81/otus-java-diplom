package ru.timebook.orderhandler.healthcheck.items;

public interface HealthCheckItem {
    String getName();
    boolean isAlive();
}
