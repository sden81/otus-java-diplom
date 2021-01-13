package ru.timebook.orderhandler.spreadsheet.models;

import org.springframework.stereotype.Component;
import ru.timebook.orderhandler.spreadsheet.exceptions.SpreadSheetException;

import java.util.HashMap;
import java.util.Map;

public class SheetColumnLetterMapImpl implements SheetColumnLetterMap{
    private final Map<String, String> columnNameLetterMap= new HashMap<>();

    public void addColumnNameLetterItem(String columnName, String columnLetter){
        columnNameLetterMap.put(columnName, columnLetter);
    }

    @Override
    public String getColumnLetterByName(String columnName) {
        if (!columnNameLetterMap.containsKey(columnName)){
            throw new SpreadSheetException(String.format("Column name %s not exist", columnName));
        }

        return columnNameLetterMap.get(columnName);
    }
}
