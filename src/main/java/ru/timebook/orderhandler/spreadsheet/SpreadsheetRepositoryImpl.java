package ru.timebook.orderhandler.spreadsheet;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.timebook.orderhandler.spreadsheet.exceptions.OrderRecordAlreadyExistException;
import ru.timebook.orderhandler.spreadsheet.exceptions.SpreadSheetException;
import ru.timebook.orderhandler.tickets.domain.Order;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//https://developers.google.com/sheets/api/reference/rest
@Repository
@Slf4j
public class SpreadsheetRepositoryImpl implements SpreadsheetRepository {
    @Autowired
    Sheets sheets;
    String spreadsheetId;
    Spreadsheet spreadsheet;

    Map<String, Map<String, String>> colTitleToLetterMap = new HashMap<>();

    private String orderIdColumnTitle;

    @Value("${spreadsheet.template_sheet_title}")
    private String templateSheetTitle;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    public SpreadsheetRepositoryImpl(
            Sheets sheets,
            @Value("${spreadsheet.id}") String spreadsheetId,
            @Value("${spreadsheet.order_id_column_title}") String orderIdColumnTitle
    ) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
        setSpreadsheet();
        this.orderIdColumnTitle = orderIdColumnTitle;
    }

    public void setSpreadsheet() {
        try {
            this.spreadsheet = sheets.spreadsheets().get(spreadsheetId).execute();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public List<String> readColumn(String sheetTitle, String colName) {
        String colLetter = getColLetterByColTitle(sheetTitle, colName);
        String range = String.format("%s!%s2:%s", sheetTitle, colLetter, colLetter);
        ValueRange response = requestData(range);

        if (response.getValues() == null) {
            return new ArrayList<String>();
        }

        var result = response.getValues().stream()
                .filter(item-> !item.isEmpty())
                .map(item -> item.get(0).toString())
                .collect(Collectors.toList());;

        return result;
    }

    public String getColLetterByColTitle(@NonNull String sheetTitle, String colName) {
        var sheetColLetterByColTitleMap = colTitleToLetterMap.get(sheetTitle);
        if (sheetColLetterByColTitleMap == null) {
            colTitleToLetterMap.put(sheetTitle, createColNameByLetterMap(sheetTitle));
        }

        var letter = colTitleToLetterMap.get(sheetTitle).get(colName);
        if (letter == null) {
            throw new SpreadSheetException(String.format("Can't find column \"%s\" in sheet \"%s\"", colName, sheetTitle));
        }

        return letter;
    }

    private ValueRange requestData(@NonNull String range) {
        try {
            return sheets.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
        } catch (Exception ex) {
            throw new SpreadSheetException(ex);
        }
    }

    private Map<String, String> createColNameByLetterMap(@NonNull String sheetTitle) {
        String range = sheetTitle + "!A1:Z1";

        var response = requestData(range);
        var values = response.getValues();

        if (values == null || values.get(0) == null || values.get(0).isEmpty()) {
            throw new SpreadSheetException("ColNameByLetterMapper get empty result");
        }

        var titlesList = values.get(0);
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

        var colNameByLetterMap = new HashMap<String, String>();
        String colTitle;
        for (int i = 0; i < titlesList.size(); i++) {
            colTitle = titlesList.get(i).toString().trim();
            if (!colTitle.isEmpty()) {
                colNameByLetterMap.put(titlesList.get(i).toString().trim(), String.valueOf(alphabet[i]));
            }
        }

        return colNameByLetterMap;
    }

    public int getLastRecordInColumnIndex(@NonNull String sheetTitle, @NonNull String columnName) {
        List<String> columnValues = readColumn(sheetTitle, columnName);

        if (columnValues == null) {
            throw new SpreadSheetException("Error get last row index for column");
        }

        return columnValues.size() + 1;
    }

    public void insertData(
            @NonNull String sheetTitle,
            @NonNull String startColumnLetter,
            @NonNull int rowIndex,
            @NonNull List<String> values
    ) {

        try {
            ValueRange content = new ValueRange();
            content.setMajorDimension("ROWS");
            var addedContent = values.stream().map(v -> (Object) v).collect(Collectors.toList());
            content.setValues(Arrays.asList(addedContent));
            String range = String.format("%s!%s%s", sheetTitle, startColumnLetter, rowIndex);

            var request = sheets.spreadsheets().values()
                    .append(spreadsheetId, range, content)
                    .setInsertDataOption("OVERWRITE")
                    .setValueInputOption("RAW")
                    .execute();

        } catch (Exception ex) {
            throw new SpreadSheetException(ex);
        }
    }

    /**
     * @param sheetTitle
     * @param range      A1:C1 example
     */
    public void deleteData(
            @NonNull String sheetTitle,
            @NonNull String range
    ) {
        try {
            ClearValuesRequest requestBody = new ClearValuesRequest();
            sheets.spreadsheets().values()
                    .clear(spreadsheetId, sheetTitle + "!" + range, requestBody)
                    .execute();
        } catch (Exception ex) {
            throw new SpreadSheetException(ex);
        }
    }

    public boolean isColumnValuesContainString(
            @NonNull String sheetTitle,
            @NonNull String columnName,
            @NonNull String searchString
    ) {
        if (searchString.isEmpty()) {
            throw new SpreadSheetException("Empty search string");
        }

        List<String> values = readColumn(sheetTitle, columnName);
        String searchStringLowCase = searchString.toLowerCase();

        return values.stream().anyMatch(v -> v.toLowerCase().contains(searchStringLowCase));
    }

    @Override
    public boolean isExistOrderId(@NonNull String sheetTitle, @NonNull String orderId) {
        return isColumnValuesContainString(sheetTitle, orderIdColumnTitle, orderId);
    }

    @Override
    public boolean insertOrderData(@NonNull String sheetTitle, @NonNull Order order) {
        String spreadsheetTitle = order.getSpreadsheetTitle();

        if (isExistOrderId(sheetTitle, order.getOrderId())) {
            throw new OrderRecordAlreadyExistException(String.format("Try add existing order %s to sheet %s", order.getOrderId(), spreadsheetTitle));
        }

        int row = getLastRecordInColumnIndex(spreadsheetTitle, orderIdColumnTitle);

        List<String> insertedData = Stream.of(
                "", //№ п/п
                order.getConsumerName(), //Название магазина
                "", //Формат
                "", //Географический регион
                "", //Адрес
                "", //Группа
                order.getOrderId(), //Получение заявки на услугу в Ariba
                "", //Выставление счета
                "", //Ожидаемая сумма оплаты
                String.valueOf(order.getTotalSum()), //Сумма присланной заявки
                String.valueOf(order.getItemCount()), //Количество АПК
                "", //Комментарии
                "" //Статус в Диадоке

        ).collect(Collectors.toList());

        insertData(sheetTitle, "A", ++row, insertedData);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return isExistOrderId(sheetTitle, order.getOrderId());
    }

    //TODO вынести в конфиг
    @Cacheable("sheetTitles")
    public Map<Integer, String> getSheetsTitles() {
        log.info("getting sheet titles for spreadsheet with id: {}", spreadsheetId);
        List<Sheet> sheetsProperty = (List) spreadsheet.getSheets();

        Map<Integer, String> sheetsTitle = new HashMap<>();
        sheetsProperty.stream().forEach(sheet -> {
            sheetsTitle.put(sheet.getProperties().getSheetId(), sheet.getProperties().getTitle());
        });

        return sheetsTitle;
    }

    @Override
    @CacheEvict(value = "sheetTitles", allEntries = true)
    public void createSheet(@NonNull String newSheetTitle) {
        try {
            var templateSheetId = getSheetsTitles().entrySet().stream()
                    .filter(entry -> entry.getValue().equals(templateSheetTitle)).findFirst()
                    .orElseThrow(() -> new SpreadSheetException(String.format("Can't find template sheet with name '%s'. Need create sheet with this title.", templateSheetTitle)))
                    .getKey();

            List<Request> requests = new ArrayList<>();

            DuplicateSheetRequest duplicateSheetRequest = new DuplicateSheetRequest();
            duplicateSheetRequest.setSourceSheetId(templateSheetId);
            duplicateSheetRequest.setNewSheetName(newSheetTitle);

            requests.add(new Request().setDuplicateSheet(duplicateSheetRequest));

            BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
            requestBody.setRequests(requests);

            Sheets.Spreadsheets.BatchUpdate request =
                    sheets.spreadsheets().batchUpdate(spreadsheetId, requestBody);

            BatchUpdateSpreadsheetResponse response = request.execute();
            setSpreadsheet();
        } catch (Exception ex) {
            throw new SpreadSheetException(ex);
        }
    }

    @Override
    @CacheEvict(value = "sheetTitles", allEntries = true)
    public void deleteSheet(@NonNull String sheetTitle) {
        try {
            var sheetIdForDelete = getSheetsTitles().entrySet().stream()
                    .filter(entry -> entry.getValue().equals(sheetTitle)).findFirst()
                    .orElseThrow(() -> new SpreadSheetException("Can't find sheet for delete"))
                    .getKey();

            List<Request> requests = new ArrayList<>(); // TODO: Update placeholder value.

            DeleteSheetRequest deleteSheetRequest = new DeleteSheetRequest();
            deleteSheetRequest.setSheetId(sheetIdForDelete);

            requests.add(new Request().setDeleteSheet(deleteSheetRequest));

            BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
            requestBody.setRequests(requests);

            Sheets.Spreadsheets.BatchUpdate request =
                    sheets.spreadsheets().batchUpdate(spreadsheetId, requestBody);

            BatchUpdateSpreadsheetResponse response = request.execute();
            setSpreadsheet();
        } catch (Exception ex) {
            throw new SpreadSheetException(ex);
        }
    }

    @Override
    public boolean isAlive() {
        try {
            var result = sheets.spreadsheets().get(spreadsheetId).execute();
            if (result == null) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }
}
