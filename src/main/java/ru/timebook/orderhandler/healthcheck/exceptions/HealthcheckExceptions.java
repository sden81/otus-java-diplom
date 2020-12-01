package ru.timebook.orderhandler.healthcheck.exceptions;

public class HealthcheckExceptions extends RuntimeException {
    public HealthcheckExceptions(Throwable cause) {
        super(cause);
    }

    public HealthcheckExceptions(String message) {
        super(message);
    }
}
