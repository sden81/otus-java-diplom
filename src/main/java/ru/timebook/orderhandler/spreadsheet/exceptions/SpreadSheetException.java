package ru.timebook.orderhandler.spreadsheet.exceptions;

public class SpreadSheetException extends RuntimeException {
    public SpreadSheetException(String message) {
        super(message);
    }

    public SpreadSheetException(Throwable cause) {
        super(cause);
    }
}
