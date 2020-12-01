package ru.timebook.orderhandler.tickets.domain;

import ru.timebook.orderhandler.tickets.exceptions.ParsingException;

public enum Company {
    ATAK("Атак"),
    AUCHAN("Ашан");

    private final String displayedName;

    Company(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public static Company parseCompany(String rawString) {
        for (Company company : values()) {
            if (rawString.toLowerCase().contains(company.getDisplayedName().toLowerCase())){
                return company;
            }
        }

        throw new ParsingException(String.format("Can't parse company, Raw string: %s", rawString));
    }
}