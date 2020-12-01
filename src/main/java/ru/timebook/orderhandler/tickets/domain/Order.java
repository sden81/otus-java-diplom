package ru.timebook.orderhandler.tickets.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Order {
    String orderId;
    String consumerName;
    int totalSum;
    int itemCount;
    Company company;
    Month reportingMonth;
    int year;

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
