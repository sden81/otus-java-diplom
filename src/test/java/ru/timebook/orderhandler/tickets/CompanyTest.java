package ru.timebook.orderhandler.tickets;

import org.junit.jupiter.api.Test;
import ru.timebook.orderhandler.tickets.exceptions.ParsingException;
import ru.timebook.orderhandler.tickets.domain.Company;

import static org.assertj.core.api.Assertions.*;

class CompanyTest {

    @Test
    void parseRawString() {
        assertThat(Company.parseCompany("good Ашан raw string")).isEqualTo(Company.AUCHAN);
        assertThat(Company.parseCompany("goodатак  raw string")).isEqualTo(Company.ATAK);

        assertThatThrownBy(() -> {
            var company = Company.parseCompany("bad string");
        }).isInstanceOf(ParsingException.class)
                .hasMessageContaining("Can't parse");
    }
}