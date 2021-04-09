package ru.timebook.orderhandler.spreadsheet.models;

public interface SheetColumnLetterMap {
    void addColumnNameLetterItem(String columnName, String columnLetter);

    String getColumnLetterByName(String columnName);
}
