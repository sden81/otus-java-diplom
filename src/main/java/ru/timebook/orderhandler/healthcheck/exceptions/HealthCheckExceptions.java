package ru.timebook.orderhandler.healthcheck.exceptions;

public class HealthCheckExceptions extends RuntimeException {
    public HealthCheckExceptions(Throwable cause) {
        super(cause);
    }

    public HealthCheckExceptions(String message) {
        super(message);
    }
}
