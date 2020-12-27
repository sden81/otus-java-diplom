package ru.timebook.orderhandler.tickets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ru.timebook.orderhandler.AbstractTest;
import ru.timebook.orderhandler.okDeskClient.OkDeskRepository;
import ru.timebook.orderhandler.spreadsheet.SpreadsheetRepository;
import ru.timebook.orderhandler.tickets.domain.Company;
import ru.timebook.orderhandler.tickets.domain.Month;
import ru.timebook.orderhandler.tickets.domain.Order;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TicketServiceImplTest extends AbstractTest {
    @Autowired
    ResourceLoader resourceLoader;

    TicketService ticketService;

    @Mock
    TicketRepository mockTicketRepository;

    @Mock
    SpreadsheetRepository mockSpreadsheetRepository;

    @Autowired
    TicketParser ticketParser;

    Ticket fakeTicket;

    @BeforeEach
    void init() {
        ticketService = new TicketServiceImpl(mockTicketRepository, mockSpreadsheetRepository, ticketParser);
        fakeTicket = getFakeTicket();
    }

    @Test
    void parseTicket() {
        var order = ticketService.parseTicket(fakeTicket);

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
            File file = resource.getFile();
            String content = new String(Files.readAllBytes(file.toPath()));

            return content;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    void getNeedProcessedTickets() {
        when(mockTicketRepository.getNeedProcessTicketIds()).thenReturn(Arrays.asList(new Long[]{1L, 2L}));
        ticketService.addExcludedOkDeskIssueId(1L);
        when(mockTicketRepository
                .getTickets(Mockito.eq(Arrays.asList(new Long[]{2L}))))
                .thenReturn(Arrays.asList(new Ticket[]{fakeTicket}));

        var needProcessedTickets = ticketService.getNeedProcessedTickets();
        assertThat(needProcessedTickets.size()).isEqualTo(1);
    }
}