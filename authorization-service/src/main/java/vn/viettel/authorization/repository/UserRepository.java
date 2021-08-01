package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.User;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    @Query(value = "SELECT u FROM User u WHERE UPPER(u.userAccount) = UPPER(:username) AND u.status = 1 ")
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u " +
            "FROM User u " +
            " JOIN RoleUser ru ON ru.userId = u.id " +
            " JOIN Role r ON ru.roleId = r.id" +
            " JOIN RolePermission rp ON rp.roleId = r.id" +
            " JOIN Permission p ON rp.permissionId = p.id" +
            " JOIN OrgAccess o ON p.id = o.permissionId " +
            "where o.shopId = :shopId and u.status = 1")
    List<User> findAllByShopId(Long shopId);

    @Query(value = "SELECT u FROM User u " +
            "where u.status = 1 AND u.id IN (:UserIds)")
    List<User> getUserByIds(List<Long> UserIds);
}
