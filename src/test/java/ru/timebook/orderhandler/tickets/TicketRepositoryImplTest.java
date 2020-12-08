package ru.timebook.orderhandler.tickets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.timebook.orderhandler.AbstractTest;
import ru.timebook.orderhandler.okDeskClient.IssueListFilter;
import ru.timebook.orderhandler.okDeskClient.OkDeskRepository;
import ru.timebook.orderhandler.okDeskClient.dto.*;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TicketRepositoryImplTest extends AbstractTest {
    private TicketRepositoryImpl ticketRepository;
    private OkDeskRepository mockOkDeskRepository;

    @BeforeEach
    public void init() {
        mockOkDeskRepository = mock(OkDeskRepository.class);

        ticketRepository = new TicketRepositoryImpl(mockOkDeskRepository, IssueListFilter.builder().build());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getTicket() {
        when(mockOkDeskRepository.getIssue(any())).thenReturn(getFakeIssue());

        var ticket = ticketRepository.getTicket(1L);
        assertThat(ticket.isPresent()).isTrue();
        assertThat(ticket.get()).isExactlyInstanceOf(Ticket.class);
        assertThat(ticket.get().getId()).isEqualTo(134);
    }

    @Test
    void getTickets() {
        when(mockOkDeskRepository.getIssue(any())).thenReturn(getFakeIssue());

        var tickets = ticketRepository.getTickets(Arrays.asList(1L));
        assertThat(tickets).isNotEmpty();
        assertThat(tickets.size()).isEqualTo(1);
    }

    @Test
    void getNeedProcessTicketIds() {
        when(mockOkDeskRepository.getIssuesList(Mockito.any())).thenReturn(new ArrayList<>() {{
            add(1L);
            add(2L);
            add(3L);
        }});
        ticketRepository.addExcludedOkDeskIssueIds(1L);
        List<Long> ticketsIds = ticketRepository.getNeedProcessTicketIds();
        assertThat(ticketsIds).isNotEmpty();
        assertThat(ticketsIds.size()).isEqualTo(2);
    }

    @Test
    void generateJsonIssue() {
        var fakeIssue = getFakeIssue().get();

        ObjectMapper objectMapper = (new ObjectMapper())
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            var json = objectMapper.writeValueAsString(fakeIssue);
            assertThat(json.isEmpty()).isFalse();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private Optional<Issue> getFakeIssue() {
        return Optional.of(
                Issue.builder()
                        .id(134)
                        .author(getFakeAuthor())
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .completedAt(LocalDateTime.now().minusDays(5))
                        .status(getFakeStatus())
                        .build()
        );
    }

    private Ticket getFakeTicket() {
        return Ticket.builder()
                .id(1)
                .author(getFakeAuthor())
                .title("Ticket title")
                .description("Ticket description")
                .userComments(getFakeComments())
                .createdAt(LocalDateTime.now().minusDays(10))
                .completedAt(LocalDateTime.now().minusDays(1))
                .status(getFakeStatus())
                .build();
    }

    private Author getFakeAuthor() {
        return new Author(12, "Volkov", "some author type");
    }

    private List<UserComment> getFakeComments() {
        return Arrays.asList(
                new UserComment(4, true, "comment1", LocalDateTime.now().minusDays(18), getFakeAuthor()),
                new UserComment(5, true, "comment2", LocalDateTime.now().minusDays(17), getFakeAuthor()),
                new UserComment(6, true, "comment3", LocalDateTime.now().minusDays(16), getFakeAuthor())
        );
    }

    private Status getFakeStatus() {
        return new Status(StatusCodes.completed, "all done");
    }
}