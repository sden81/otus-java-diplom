package ru.timebook.orderhandler.spreadsheet.models;

public interface ColumnNameToColumnLetterMapper {
    Boolean isSheetTitleExist(String sheetTitle);

    String getColLetterByColName(String sheetTitle, String colName);

    void addColNameToColLetterMap(String sheetTitle, SheetColumnLetterMap sheetColumnLetterMap);
}
