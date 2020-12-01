package ru.timebook.orderhandler.okDeskServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.timebook.orderhandler.okDeskServer.domain.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT * FROM comment c WHERE c.issue_id = ?", nativeQuery = true)
    List<Comment> findCommentsBy(Long issueId);
}