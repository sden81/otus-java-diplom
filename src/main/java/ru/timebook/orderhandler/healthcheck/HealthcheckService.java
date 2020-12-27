package ru.timebook.orderhandler.healthcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timebook.orderhandler.healthcheck.dto.Item;
import ru.timebook.orderhandler.healthcheck.dto.ReadinessHealthcheck;
import ru.timebook.orderhandler.healthcheck.exceptions.HealthcheckExceptions;
import ru.timebook.orderhandler.okDeskClient.OkDeskRepository;
import ru.timebook.orderhandler.spreadsheet.SpreadsheetRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Service
public class HealthcheckService {
    Map<String, Callable<Boolean>> itemsMap = new HashMap<>();

    private final OkDeskRepository okDeskRepository;

    private final SpreadsheetRepository spreadsheetRepository;

    public HealthcheckService(OkDeskRepository okDeskRepository, SpreadsheetRepository spreadsheetRepository) {
        this.okDeskRepository = okDeskRepository;
        this.spreadsheetRepository = spreadsheetRepository;
        init();
    }

    public void init() {
        itemsMap.put("okDesk api", () -> okDeskRepository.isAlive());
        itemsMap.put("google spreadsheet api", () -> spreadsheetRepository.isAlive());
    }

    public ReadinessHealthcheck getReadinessHealthcheck() {
        var readinessHealthcheckResultMap = getReadinessHealthcheckResultMap();

        var readinessHealthcheckBuilder = ReadinessHealthcheck.builder();
        var isHaveFailedStatus = readinessHealthcheckResultMap.entrySet().stream()
                .anyMatch(stringBooleanEntry -> !stringBooleanEntry.getValue());
        var readinessHealthcheckStatusCode = isHaveFailedStatus ? 500 : 200;
        readinessHealthcheckBuilder.readinessHealthcheckStatus(readinessHealthcheckStatusCode);

        readinessHealthcheckResultMap.forEach((name, result) -> {
            readinessHealthcheckBuilder.addItem(new Item(name, result));
        });

        return readinessHealthcheckBuilder.build();
    }

    private Map<String, Boolean> getReadinessHealthcheckResultMap() {
        Map<String, Boolean> resultMap = new HashMap<>();
        itemsMap.forEach((itemName, callable) -> {
            try {
                resultMap.put(itemName, callable.call());
            } catch (Exception e) {
                throw new HealthcheckExceptions(e);
            }
        });

        return resultMap;
    }
}
