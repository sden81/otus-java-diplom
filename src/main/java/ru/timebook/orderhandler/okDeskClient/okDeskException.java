package ru.timebook.orderhandler.okDeskClient;

public class okDeskException extends RuntimeException{
    public okDeskException(String message, Throwable cause) {
        super(message, cause);
    }

    public okDeskException(Throwable cause) {
        super(cause);
    }
}
