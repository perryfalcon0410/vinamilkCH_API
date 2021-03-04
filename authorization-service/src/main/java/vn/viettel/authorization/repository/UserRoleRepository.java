package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.UserRole;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface UserRoleRepository extends BaseRepository<UserRole> {
    @Query(value = "SELECT * FROM user_roles WHERE user_id = :userId", nativeQuery = true)
    List<UserRole> findByUserId(int userId);
}