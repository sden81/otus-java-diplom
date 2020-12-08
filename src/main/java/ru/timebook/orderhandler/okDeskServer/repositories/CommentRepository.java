package ru.timebook.orderhandler.okDeskServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.timebook.orderhandler.okDeskServer.domain.Comment;
import ru.timebook.orderhandler.okDeskServer.domain.Issue;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByIssue_Id(Long issue_id);
}