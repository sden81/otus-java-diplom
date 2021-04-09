package ru.timebook.orderhandler.spreadsheet;

import ru.timebook.orderhandler.tickets.domain.Order;

import java.util.Map;

public interface SpreadsheetRepository {
    boolean isExistOrderId(String sheetTitle, String orderId);

    boolean insertOrderData(String sheetTitle, Order order);

    Map<Integer, String> getSheetsTitles();

    void createSheet(String newSheetTitle);

    void deleteSheet(String sheetTitle);

    boolean isAlive();
}
