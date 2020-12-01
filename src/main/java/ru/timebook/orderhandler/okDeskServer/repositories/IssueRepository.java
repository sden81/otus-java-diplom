package ru.timebook.orderhandler.okDeskServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.timebook.orderhandler.okDeskServer.domain.Issue;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    @Query(value = "SELECT * FROM issue i WHERE i.id = ?", nativeQuery = true)
    Issue findIssueBy(Long issueId);

    @Query(value = "SELECT id FROM issue", nativeQuery = true)
    List<Long> getAllIssueIds();
}