package ru.timebook.orderhandler.tickets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ru.timebook.orderhandler.AbstractTest;
import ru.timebook.orderhandler.tickets.domain.Company;
import ru.timebook.orderhandler.tickets.domain.Month;
import ru.timebook.orderhandler.tickets.domain.Order;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class TicketServiceImplTest extends AbstractTest {
    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    TicketService ticketService;

    @Test
    void parseTicket() {
        var ticket = getFakeTicket();
        var order = ticketService.parseTicket(ticket);

        assertTrue(order instanceof Order);
        assertThat(order.getOrderId()).isEqualTo("EP3269109");
        assertThat(order.getConsumerName()).isEqualTo("Магазин№1");
        assertThat(order.getTotalSum()).isEqualTo(1500);
        assertThat(order.getItemCount()).isEqualTo(2);
        assertThat(order.getCompany()).isEqualTo(Company.ATAK);
        assertThat(order.getReportingMonth()).isEqualTo(Month.JUNE);
        assertThat(order.getYear()).isEqualTo(2020);
    }

    private Ticket getFakeTicket() {
        return Ticket.builder()
                .id(1)
                .title("TMP")
                .description(getTestOrderContentForParsing())
                .build();
    }

    private String getTestOrderContentForParsing() {
        Resource resource = resourceLoader.getResource("classpath:testData/orderContentForTest.txt");
        try {
            InputStream input = resource.getInputStream();
            File file = resource.getFile();
            String content = new String(Files.readAllBytes(file.toPath()));

            return content;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}