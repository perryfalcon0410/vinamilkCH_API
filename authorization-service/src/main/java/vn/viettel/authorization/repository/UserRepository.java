package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.repository.BaseRepository;

public interface UserRepository extends BaseRepository<User> {

    @Query(value = "SELECT * FROM USERS WHERE USERNAME = :username", nativeQuery = true)
    User findByUsername(String username);
}
