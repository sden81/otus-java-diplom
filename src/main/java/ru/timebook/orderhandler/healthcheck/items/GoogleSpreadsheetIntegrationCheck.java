package ru.timebook.orderhandler.healthcheck.items;

import org.springframework.stereotype.Component;
import ru.timebook.orderhandler.spreadsheet.SpreadsheetRepository;

@Component
public class GoogleSpreadsheetIntegrationCheck implements HealthCheckItem {
    private static final String NAME = "Google Spreadsheet Integration Check";
    private final SpreadsheetRepository spreadsheetRepository;

    public GoogleSpreadsheetIntegrationCheck(SpreadsheetRepository spreadsheetRepository) {
        this.spreadsheetRepository = spreadsheetRepository;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isAlive() {
        return spreadsheetRepository.isAlive();
    }

}
