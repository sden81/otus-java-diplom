package ru.timebook.orderhandler.healthcheck.items;

import org.springframework.stereotype.Component;
import ru.timebook.orderhandler.okDeskClient.OkDeskRepository;

@Component
public class OkDeskIntegrationCheck implements HealthCheckItem{
    private static final String NAME = "OkDesk Integration Check";
    private final OkDeskRepository okDeskRepository;

    public OkDeskIntegrationCheck(OkDeskRepository okDeskRepository) {
        this.okDeskRepository = okDeskRepository;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isAlive() {
        return okDeskRepository.isAlive();
    }
}
