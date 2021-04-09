package ru.timebook.orderhandler.okDeskServerService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.timebook.orderhandler.AbstractTest;
import ru.timebook.orderhandler.okDeskServer.OkDeskServerService;
import ru.timebook.orderhandler.okDeskServer.domain.Token;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OkDeskServerServiceTest extends AbstractTest {
    @Autowired
    private OkDeskServerService okDeskServerService;

    @Test
    void getToken() {
        String goodTokenString="123";
        Optional<Token> goodToken = okDeskServerService.getToken(goodTokenString);
        assertThat(goodToken.isPresent()).isTrue();
        assertThat(goodToken.get().getTokenString()).isEqualTo(goodTokenString);
        assertThat(goodToken.get().isValid()).isTrue();

        String badTokenString="321";
        assertThat(okDeskServerService.getToken(badTokenString).isEmpty()).isTrue();
    }

    @Test
    void getIssue() {
        Long goodIssueId = 80475L;
        var existIssue = okDeskServerService.getIssue(goodIssueId);
        assertThat(existIssue.isPresent()).isTrue();
        assertThat(existIssue.get().getId()).isEqualTo(goodIssueId);

        Long badIssueId = 234L;
        var notExistIssue = okDeskServerService.getIssue(badIssueId);
        assertThat(notExistIssue.isEmpty()).isTrue();
    }

    @Test
    void getIssuesList(){
        var issueIds = okDeskServerService.getAllIssueIds();
        assertThat(issueIds.size()).isGreaterThan(0);
        assertThat(issueIds.contains(80475L)).isTrue();
    }

    @Test
    void getCommentsByIssueId() {
        Long goodIssueId = 80475L;
        var existComments = okDeskServerService.getCommentsByIssueId(goodIssueId);
        assertThat(existComments.size()).isGreaterThan(0);

        Long badIssueId = 234L;
        var notExistComments = okDeskServerService.getCommentsByIssueId(badIssueId);
        assertThat(notExistComments.size()).isEqualTo(0);
    }
}