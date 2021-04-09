package ru.timebook.orderhandler.healthcheck;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.timebook.orderhandler.AbstractTest;
import ru.timebook.orderhandler.healthcheck.dto.Item;
import ru.timebook.orderhandler.healthcheck.items.GoogleSpreadsheetIntegrationCheck;
import ru.timebook.orderhandler.healthcheck.items.HealthCheckItem;
import ru.timebook.orderhandler.healthcheck.items.OkDeskIntegrationCheck;
import ru.timebook.orderhandler.okDeskClient.OkDeskRepository;
import ru.timebook.orderhandler.spreadsheet.SpreadsheetRepository;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class HealthCheckServiceTest extends AbstractTest {
    @Mock
    SpreadsheetRepository spreadsheetRepository;

    @Mock
    OkDeskRepository okDeskRepository;

    @Test
    void getReadinessHealthCheck() {
        var goodHealthCheckService = createHealthCheckService(true);
        var goodResult = goodHealthCheckService.getReadinessHealthCheck();
        assertThat(goodResult.getReadinessHealthCheckStatus()).isEqualTo(200);
        var firstItem = (Item) goodResult.getItems().toArray()[0];
        assertThat(firstItem.getSuccess()).isTrue();

        var badHealthCheckService = createHealthCheckService(false);
        var badResult = badHealthCheckService.getReadinessHealthCheck();
        assertThat(badResult.getReadinessHealthCheckStatus()).isEqualTo(500);
        var anotherFirstItem = (Item) badResult.getItems().toArray()[0];
        assertThat(anotherFirstItem.getSuccess()).isFalse();
    }

    private HealthCheckService createHealthCheckService(Boolean isAlive) {
        when(spreadsheetRepository.isAlive()).thenReturn(isAlive);
        when(okDeskRepository.isAlive()).thenReturn(isAlive);

        var items = new HashSet<HealthCheckItem>();
        items.add(new GoogleSpreadsheetIntegrationCheck(spreadsheetRepository));
        items.add(new OkDeskIntegrationCheck(okDeskRepository));

        return new HealthCheckService(items);
    }
}