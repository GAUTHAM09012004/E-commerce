package com.example.cssandjavascript1.token;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(value = """
            SELECT t.*
            FROM token t
            INNER JOIN user u ON t.user_id = u.id
            WHERE u.id = :id AND (t.expired = FALSE OR t.revoked = FALSE)
            """, nativeQuery = true)
    List<Token> findAllValidTokenByUser(Integer id);

    Optional<Token> findByToken(String token);
}
