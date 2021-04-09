package ru.timebook.orderhandler.spreadsheet.models;

import ru.timebook.orderhandler.spreadsheet.exceptions.SpreadSheetException;

import java.util.HashMap;
import java.util.Map;

public class ColumnNameToColumnLetterMapperImpl implements ColumnNameToColumnLetterMapper {
    private final Map<String, SheetColumnLetterMap> sheetTitleToColNameMap= new HashMap<>();

    @Override
    public Boolean isSheetTitleExist(String sheetTitle) {
        return sheetTitleToColNameMap.containsKey(sheetTitle);
    }

    @Override
    public String getColLetterByColName(String sheetTitle, String colName) {
        if (!sheetTitleToColNameMap.containsKey(sheetTitle)){
            throw new SpreadSheetException(String.format("Sheet title %s not exist", sheetTitle));
        }

        return sheetTitleToColNameMap.get(sheetTitle).getColumnLetterByName(colName);
    }

    @Override
    public void addColNameToColLetterMap(String sheetTitle, SheetColumnLetterMap sheetColumnLetterMap) {
        sheetTitleToColNameMap.put(sheetTitle, sheetColumnLetterMap);
    }
}
