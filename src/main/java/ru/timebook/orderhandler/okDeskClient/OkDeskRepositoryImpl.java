package ru.timebook.orderhandler.okDeskClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.timebook.orderhandler.okDeskClient.dto.UserComment;
import ru.timebook.orderhandler.okDeskClient.dto.Issue;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OkDeskRepositoryImpl implements OkDeskRepository {

    private RestTemplate restTemplate;
    private String apiToken;

    Logger logger = LoggerFactory.getLogger(OkDeskRepositoryImpl.class);

    public OkDeskRepositoryImpl(RestTemplate restTemplate, @NonNull @Value("${okDesk.api_token}") String apiToken) {
        this.restTemplate = restTemplate;
        this.apiToken = apiToken;
        logger.info(String.format("Okdesk api Token: %s", apiToken));
    }

    @Override
    public List<UserComment> getComments(@NonNull Long id) {
        String url = "/issues/" + id.toString() + "/comments?api_token=" + apiToken;
        try {
            var response = restTemplate.getForEntity(url, UserComment[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            throw new okDeskException(e);
        }
    }

    @Override
    public Optional<Issue> getIssue(@NonNull Long id) {
        String url = "/issues/" + id.toString() + "?api_token=" + apiToken;
        try {
            var response = restTemplate.getForEntity(url, Issue.class);
            Issue issue = response.getBody();

            return issue.getDescription() == null || issue.getTitle() == null ? Optional.empty() : Optional.of(issue);
        } catch (Exception e) {
            throw new okDeskException(e);
        }
    }

    @Override
    public List<Long> getIssuesList(IssueListFilter issueListFilter) {
        String filterParams = String.join("&", issueListFilter.generateParams());
        filterParams = (!filterParams.isEmpty()) ? "&" + filterParams : "";
        String url = "/issues/count?api_token=" + apiToken + filterParams;
        try {
            var response = restTemplate.getForEntity(url, List.class);
            List<Integer> issues = response.getBody();
            List<Long> finalIssues = issues.stream().mapToLong(Integer::longValue)
                    .boxed().collect(Collectors.toList());

            return finalIssues;
        } catch (Exception e) {
            throw new okDeskException(e);
        }
    }

    @Override
    public void addComment(Long id, String commentText) {
        String url = "/issues/" + id.toString() + "/comments?api_token=" + apiToken;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> map = new HashMap<>();
            map.put("content", commentText);
            map.put("public", "false");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            throw new okDeskException(e);
        }
    }

    @Override
    public boolean isAlive(){
        String url = "/issues/types/?api_token=" + apiToken;
        try {
            var response = restTemplate.getForEntity(url, List.class);
            List<String> types = response.getBody();

            if (types.isEmpty()){
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
