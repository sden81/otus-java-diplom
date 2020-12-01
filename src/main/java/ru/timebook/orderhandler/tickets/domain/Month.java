package ru.timebook.orderhandler.tickets.domain;

import ru.timebook.orderhandler.tickets.exceptions.ParsingException;

public enum Month {
    JANUARY("Январь"),
    FEBRUARY("Февраль"),
    MARCH("Март"),
    APRIL("Апрель"),
    MAY("Май"),
    JUNE("Июнь"),
    JULY("Июль"),
    AUGUST("Август"),
    SEPTEMBER("Сентябрь"),
    OCTOBER("Октябрь"),
    NOVEMBER("Ноябрь"),
    DECEMBER("Декабрь");

    private final String displayedName;

    Month(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public static Month parseMonth(String rawString, int firstLettersCountInMonthName) {
        String rawStringLowCase = rawString.toLowerCase();
        String searchedMonth;
        for (Month month : values()) {
            searchedMonth = month.getDisplayedName().toLowerCase();
            searchedMonth = firstLettersCountInMonthName > 0 ? searchedMonth.substring(0, firstLettersCountInMonthName - 1) : searchedMonth;

            if (rawStringLowCase.contains(searchedMonth)) {
                return month;
            }
        }

        throw new ParsingException(String.format("Can't parse month. Raw string: %s", rawString));
    }
}
