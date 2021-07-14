package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.User;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    @Query(value = "SELECT * FROM USERS WHERE UPPER(USER_ACCOUNT) = UPPER(:username) AND STATUS = 1 ", nativeQuery = true)
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * " +
            "FROM users " +
            " JOIN role_user ON role_user.user_id = users.id " +
            " JOIN roles ON role_user.role_id = roles.id" +
            " JOIN role_permission_map ON role_permission_map.role_id = roles.id" +
            " JOIN permissions ON role_permission_map.permission_id = permissions.id" +
            " JOIN org_access ON permissions.id = org_access.permission_id " +
            "where org_access.shop_id = :shopId and users.status = 1", nativeQuery = true)
    List<User> findAllByShopId(Long shopId);

    @Query(value = "SELECT u FROM User u " +
            "where u.status = 1 AND u.id IN (:UserIds)")
    List<User> getUserByIds(List<Long> UserIds);
}
