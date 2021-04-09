package ru.timebook.orderhandler.tickets;

import org.junit.jupiter.api.Test;
import ru.timebook.orderhandler.tickets.exceptions.ParsingException;
import ru.timebook.orderhandler.tickets.domain.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MonthTest {

    @Test
    void parseMonth() {
        assertThat(Month.parseMonth("туТ сентябрь", 3)).isEqualTo(Month.SEPTEMBER);
        assertThat(Month.parseMonth("GooDяНв book", 3)).isEqualTo(Month.JANUARY);
        assertThat(Month.parseMonth("10 окт 2020", 3)).isEqualTo(Month.OCTOBER);

        assertThatThrownBy(() -> {
            var company = Month.parseMonth("bad string", 3);
        }).isInstanceOf(ParsingException.class)
                .hasMessageContaining("Can't parse");
    }
}