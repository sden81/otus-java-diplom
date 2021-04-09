package ru.timebook.orderhandler.spreadsheet.exceptions;

public class OrderRecordAlreadyExistException  extends SpreadSheetException{
    public OrderRecordAlreadyExistException(String message) {
        super(message);
    }

    public OrderRecordAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
