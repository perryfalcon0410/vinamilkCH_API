package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.User;
import vn.viettel.core.repository.BaseRepository;

public interface UserRepository extends BaseRepository<User> {

    @Query(value = "SELECT * FROM USERS WHERE USER_ACCOUNT = :username", nativeQuery = true)
    User findByUsername(String username);
}
