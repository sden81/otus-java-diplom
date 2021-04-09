package ru.timebook.orderhandler.okDeskClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.timebook.orderhandler.okDeskClient.dto.StatusCodes;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IssueListFilterTest {
    private IssueListFilter issueListFilter;

    @BeforeEach
    void setUp() {
        issueListFilter = IssueListFilter.builder()
                .created_since(LocalDate.now())
                .addStatus(StatusCodes.opened)
                .addStatus(StatusCodes.worked)
                .addAuthorId(1)
                .addAuthorId(2)
                .build();
    }

    @Test
    void build(){
        assertTrue(issueListFilter.getStatus().contains(StatusCodes.opened));
        assertTrue(issueListFilter.getAuthor_contact_ids().contains(2));
        assertNull(issueListFilter.getUpdated_since());
        assertTrue(issueListFilter.getStatus_not().isEmpty());
    }

    @Test
    void generateParams() {
        var resultList = issueListFilter.generateParams();
        assertTrue(!resultList.isEmpty());
        assertEquals(3, resultList.size());
    }
}