package ru.timebook.orderhandler.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.timebook.orderhandler.okDeskServer.OkDeskServerService;

@RestController
public class OkDeskFakeServerController {
    @Autowired
    private OkDeskServerService okDeskServerService;

    @GetMapping("/issues/count")
    ResponseEntity<Iterable<Long>> getIssuesList(@RequestParam MultiValueMap<String, String> params) {
        if (!params.containsKey("api_token") ||
                params.get("api_token").get(0).isBlank() ||
                !okDeskServerService.isTokenValid(params.get("api_token").get(0))
        ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(okDeskServerService.getAllIssueIds(), HttpStatus.OK);
    }

    @GetMapping("/issues/{issueId}/comments")
    ResponseEntity<?> getIssueComments(@PathVariable String issueId, @RequestParam String api_token) {
        if (api_token.isBlank() || !okDeskServerService.isTokenValid(api_token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        var commentsList = okDeskServerService.getCommentsByIssueId(Long.parseLong(issueId));

        ObjectMapper objectMapper = (new ObjectMapper())
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(commentsList);
            return new ResponseEntity<>(json, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/issues/{issueId}")
    ResponseEntity<?> getIssue(@PathVariable String issueId, @RequestParam String api_token) {
        if (api_token.isBlank() || !okDeskServerService.isTokenValid(api_token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        var issue = okDeskServerService.getIssue(Long.parseLong(issueId));

        if (issue.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(okDeskServerService.getIssue(Long.parseLong(issueId)).get(), HttpStatus.OK);
    }
}
