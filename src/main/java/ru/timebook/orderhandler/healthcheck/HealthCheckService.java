package ru.timebook.orderhandler.healthcheck;

import ru.timebook.orderhandler.healthcheck.dto.Item;
import ru.timebook.orderhandler.healthcheck.dto.ReadinessHealthCheck;
import ru.timebook.orderhandler.healthcheck.items.HealthCheckItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HealthCheckService {
    private final Set<HealthCheckItem> items;

    public HealthCheckService(Set<HealthCheckItem> items) {
        this.items = items;
    }

    public ReadinessHealthCheck getReadinessHealthCheck() {
        var readinessHealthCheckResultMap = getReadinessHealthCheckResultMap();

        var readinessHealthCheckBuilder = ReadinessHealthCheck.builder();
        var isHaveFailedStatus = readinessHealthCheckResultMap.entrySet().stream()
                .anyMatch(stringBooleanEntry -> !stringBooleanEntry.getValue());
        var readinessHealthCheckStatusCode = isHaveFailedStatus ? 500 : 200;
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
