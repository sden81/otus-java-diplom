package ru.timebook.orderhandler.okDeskServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.timebook.orderhandler.okDeskServer.domain.Token;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findFirstByTokenStringEquals(String token_string);
}
