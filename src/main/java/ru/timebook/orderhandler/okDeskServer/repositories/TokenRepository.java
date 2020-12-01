package ru.timebook.orderhandler.okDeskServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.timebook.orderhandler.okDeskServer.domain.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = "SELECT * FROM token t WHERE t.token_string = ?", nativeQuery = true)
    Token findByToken(String token);
}
