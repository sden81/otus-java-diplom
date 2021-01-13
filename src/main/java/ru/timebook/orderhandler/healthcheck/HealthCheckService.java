package ru.timebook.orderhandler.healthcheck;

import org.springframework.stereotype.Service;
import ru.timebook.orderhandler.healthcheck.dto.Item;
import ru.timebook.orderhandler.healthcheck.dto.ReadinessHealthCheck;
import ru.timebook.orderhandler.healthcheck.items.HealthCheckItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class HealthCheckService {
    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_ERROR = 500;

    private final Set<HealthCheckItem> items;

    public HealthCheckService(Set<HealthCheckItem> items) {
        this.items = items;
    }

    public ReadinessHealthCheck getReadinessHealthCheck() {
        var readinessHealthCheckResultMap = getReadinessHealthCheckResultMap();

        var readinessHealthCheckBuilder = ReadinessHealthCheck.builder();
        var isHaveFailedStatus = readinessHealthCheckResultMap.entrySet().stream()
                .anyMatch(stringBooleanEntry -> !stringBooleanEntry.getValue());
        var readinessHealthCheckStatusCode = isHaveFailedStatus ? STATUS_CODE_ERROR : STATUS_CODE_OK;
        readinessHealthCheckBuilder.readinessHealthCheckStatus(readinessHealthCheckStatusCode);

        readinessHealthCheckResultMap.forEach((name, result) -> readinessHealthCheckBuilder.addItem(new Item(name, result)));

        return readinessHealthCheckBuilder.build();
    }

    private Map<String, Boolean> getReadinessHealthCheckResultMap() {
        Map<String, Boolean> resultMap = new HashMap<>();
        items.forEach((healthCheckItem -> {
            resultMap.put(healthCheckItem.getName(), healthCheckItem.isAlive());
        }));

        return resultMap;
    }
}
