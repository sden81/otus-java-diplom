package ru.timebook.orderhandler.okDeskServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import ru.timebook.orderhandler.okDeskClient.dto.Issue;
import ru.timebook.orderhandler.okDeskClient.dto.UserComment;
import ru.timebook.orderhandler.okDeskServer.domain.Token;
import ru.timebook.orderhandler.okDeskServer.repositories.CommentRepository;
import ru.timebook.orderhandler.okDeskServer.repositories.IssueRepository;
import ru.timebook.orderhandler.okDeskServer.repositories.TokenRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OkDeskServerService {
    private final TokenRepository tokenRepository;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;

    public OkDeskServerService(TokenRepository tokenRepository,
                               IssueRepository issueRepository,
                               CommentRepository commentRepository) {
        this.tokenRepository = tokenRepository;
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
    }

    public boolean isTokenValid(String tokenString) {
        var tokenObject = getToken(tokenString);
        if (tokenObject.isEmpty() || !tokenObject.get().isValid()) {
            return false;
        }

        return true;
    }

    public Optional<Token> getToken(String tokenString) {
        return tokenRepository.findFirstByTokenStringEquals(tokenString);
    }

    public Optional<Issue> getIssue(Long id) {
        var rawIssue = issueRepository.findById(id);

        if (rawIssue == null) {
            return Optional.empty();
        }

        ObjectMapper objectMapper = (new ObjectMapper())
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            var content = objectMapper.readValue(rawIssue.get().getContent(), Issue.class);
            return Optional.of(Issue.builder()
                    .id(content.getId())
                    .title(content.getTitle())
                    .description(content.getDescription())
                    .createdAt(content.getCreatedAt())
                    .completedAt(content.getCompletedAt())
                    .deadlineAt(content.getDeadlineAt())
                    .source(content.getSource())
                    .updatedAt(content.getUpdatedAt())
                    .companyId(content.getCompanyId())
                    .groupId(content.getGroupId())
                    .status(content.getStatus())
                    .oldStatus(content.getOldStatus())
                    .author(content.getAuthor())
                    .build());
        } catch (JsonProcessingException e) {
            throw new OkDeskServerException("Object mapper error", e);
        }
    }

    public List<Long> getAllIssueIds() {
        return issueRepository.getAllIssueIds();
    }

    public List<UserComment> getCommentsByIssueId(Long issueId) {
        var rawComments = commentRepository.findAllByIssue_Id(issueId);

        return rawComments.stream().map(rawComment -> {
            ObjectMapper objectMapper = (new ObjectMapper())
                    .registerModule(new JavaTimeModule())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                return objectMapper.readValue(rawComment.getContent(), UserComment.class);
            } catch (JsonProcessingException e) {
                throw new OkDeskServerException("Object mapper error", e);
            }
        }).collect(Collectors.toList());
    }
}
