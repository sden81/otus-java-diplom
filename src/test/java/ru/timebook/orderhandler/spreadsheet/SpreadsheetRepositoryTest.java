package ru.timebook.orderhandler.spreadsheet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import ru.timebook.orderhandler.AbstractTest;
import ru.timebook.orderhandler.spreadsheet.exceptions.SpreadSheetException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
 * Integration test. Need connection to internet.
 * */

class SpreadsheetRepositoryTest extends AbstractTest {
    @Autowired
    SpreadsheetRepositoryImpl spreadsheetRepository;

    @Autowired
    CacheManager cacheManager;

    private final String TEST_SHEET_NAME = "for_unit_test";
    private final String TEST_COLUMN_TITLE = "Column title";

    @Test
    void readAllColumn() {
        var columnValues = spreadsheetRepository.readColumn(TEST_SHEET_NAME, TEST_COLUMN_TITLE);
        assertThat(columnValues).isEqualTo(Arrays.asList("one", "two"));
    }

    @Test
    void getSheetsTitles() {
        var sheetsTitles = spreadsheetRepository.getSheetsTitles();
        assertThat(sheetsTitles.containsValue(TEST_SHEET_NAME)).isTrue();
    }

    @Test
    void getColLetterByColTitle() {
        String letter = spreadsheetRepository.getColLetterByColTitle(TEST_SHEET_NAME, TEST_COLUMN_TITLE);
        assertThat(letter).isEqualTo("A");
    }

    @Test
    void getLastRecordColNumber() {
        int lastRowIndex = spreadsheetRepository.getLastRecordInColumnIndex(TEST_SHEET_NAME, TEST_COLUMN_TITLE);
        assertThat(lastRowIndex).isEqualTo(3);

        assertThatThrownBy(() -> {
            int lastRowIndex2 = spreadsheetRepository.getLastRecordInColumnIndex(TEST_SHEET_NAME, "some text");
        }).isInstanceOf(SpreadSheetException.class)
                .hasMessageContaining("not exist");
    }

    @Test
    void isColumnValuesContainString() {
        boolean resultTrue = spreadsheetRepository.isColumnValuesContainString(
                TEST_SHEET_NAME,
                TEST_COLUMN_TITLE,
                "tWo"
        );
        boolean resultFalse = spreadsheetRepository.isColumnValuesContainString(
                TEST_SHEET_NAME,
                TEST_COLUMN_TITLE,
                "boom"
        );
        assertThat(resultTrue).isTrue();
        assertThat(resultFalse).isFalse();

        assertThatThrownBy(() -> {
            boolean exception = spreadsheetRepository.isColumnValuesContainString(TEST_SHEET_NAME, TEST_COLUMN_TITLE, "");
        }).isInstanceOf(SpreadSheetException.class)
                .hasMessageContaining("Empty search string");
    }

    @Test
    void insertData() {
        var rowIndex = spreadsheetRepository.getLastRecordInColumnIndex(TEST_SHEET_NAME, TEST_COLUMN_TITLE);
        rowIndex++;

        var contentList = Arrays.asList("book");
        spreadsheetRepository.insertData(
                TEST_SHEET_NAME,
                spreadsheetRepository.getColLetterByColTitle(TEST_SHEET_NAME, TEST_COLUMN_TITLE),
                rowIndex,
                contentList
        );
        assertThat(spreadsheetRepository.isColumnValuesContainString(TEST_SHEET_NAME, TEST_COLUMN_TITLE, "book")).isTrue();

        var deletedRange = spreadsheetRepository.getColLetterByColTitle(TEST_SHEET_NAME, TEST_COLUMN_TITLE) + rowIndex;
        spreadsheetRepository.deleteData(TEST_SHEET_NAME, deletedRange);
        assertThat(spreadsheetRepository.isColumnValuesContainString(TEST_SHEET_NAME, TEST_COLUMN_TITLE, "book")).isFalse();
    }

    @Test
    void duplicateAndDeleteSheet() throws InterruptedException {
        var newSheetTitle = "newSheetTitle";

        if (spreadsheetRepository.getSheetsTitles().containsValue(newSheetTitle)) {
            spreadsheetRepository.deleteSheet(newSheetTitle);
            Thread.sleep(3000);
        }

        boolean isFalse = spreadsheetRepository.getSheetsTitles().containsValue(newSheetTitle);
        assertThat(isFalse).isFalse();
        spreadsheetRepository.createSheet(newSheetTitle);
        Thread.sleep(3000);
        boolean isTrue = spreadsheetRepository.getSheetsTitles().containsValue(newSheetTitle);
        assertThat(isTrue).isTrue();
        spreadsheetRepository.deleteSheet(newSheetTitle);
        assertThat(spreadsheetRepository.getSheetsTitles().containsValue(newSheetTitle)).isFalse();
    }
}