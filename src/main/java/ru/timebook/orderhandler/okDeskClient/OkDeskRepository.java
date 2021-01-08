package ru.timebook.orderhandler.okDeskClient;

import org.springframework.lang.NonNull;
import ru.timebook.orderhandler.okDeskClient.dto.UserComment;
import ru.timebook.orderhandler.okDeskClient.dto.Issue;

import java.util.List;
import java.util.Optional;

public interface OkDeskRepository {
    List<UserComment> getComments(@NonNull Long id);

    Optional<Issue> getIssue(@NonNull Long id);

    List<Long> getIssueIdsList(IssueListFilter issueListFilter);

    void addComment(Long id, String commentText);

    boolean isAlive();
}
