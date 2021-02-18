package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<Token, String> {
    @Query(nativeQuery = true, value = "call stateful_token_storing(:token)")
    @Modifying
    void pStatefulTokenStoring(@Param("token") String token);

    Token findByToken(String token);
}
