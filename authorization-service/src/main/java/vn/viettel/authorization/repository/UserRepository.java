package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.User;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    @Query(value = "SELECT * FROM users WHERE username = :username", nativeQuery = true)
    User findByUsername(String username);
}
