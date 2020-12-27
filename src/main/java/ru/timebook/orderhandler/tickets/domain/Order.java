package ru.timebook.orderhandler.tickets.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Builder
public class Order {
    private String orderId;
    private String consumerName;
    private final int totalSum;
    private final int itemCount;
    private final Company company;
    private final Month reportingMonth;
    private final int year;

    /**
     * example "Июнь (2020) Ашан"
     * @return
     */
    public String getSpreadsheetTitle() {
        return String.format("%s (%s) %s",
                getReportingMonth().getDisplayedName(),
                getYear(),
                getCompany().getDisplayedName()
        );
    }
}
