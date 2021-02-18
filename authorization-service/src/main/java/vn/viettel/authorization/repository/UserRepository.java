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

    /* GET BY ID */
    @Query(value = "SELECT * FROM users "
            + "WHERE id = :id "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<User> getById(@Param("id") Long id);

    @Query(value = "SELECT * FROM users "
            + "WHERE email = :email "
            + "AND role_id = :role "
            + "AND status = :status "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<User> findByEmailAndRoleAndStatus(@Param("email") String email, @Param("role") Long role, @Param("status") String status);

    /* GET BY EMAIL AND ROLE ID */
    @Query(value = "SELECT * FROM users "
            + "WHERE email = :email "
            + "AND role_id = :roleId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<User> getByEmailAndRoleId(@Param("email") String email, @Param("roleId") Long roleId);

    /* GET BY ID AND ROLE ID */
    @Query(value = "SELECT * FROM users "
            + "WHERE id = :id "
            + "AND role_id = :roleId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<User> getByIdAndRoleId(@Param("id") Long id, @Param("roleId") Long roleId);

    /* GET BY ACTIVATION CODE */
    @Query(value = "SELECT * FROM users "
            + "WHERE activation_code LIKE :activationCode "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<User> getByActivationCode(@Param("activationCode") String activationCode);

    /* GET BY EMAIL */
    @Query(value = "SELECT * FROM users "
            + "WHERE email like :email "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<User> getByEmail(@Param("email") String email);

    boolean existsByEmail(String emails);

    /* GET LIST BY ROLE ID */
    @Query(value = "SELECT * FROM users "
            + "WHERE role_id = :roleId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Collection<User> getByRoleId(@Param("roleId") Long roleId);

    @Query(value = "SELECT email FROM users "
            + "WHERE id IN :ids ", nativeQuery = true)
    List<String> getAllEmailById(@Param("ids") long[] ids);

    @Query(value = "SELECT * FROM users "
            + "WHERE (:roleId is null or role_id = :roleId) "
            + "AND (email like %:searchKeywords% or name like %:searchKeywords%) "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Page<User> getUserIndexBySearchKeywordsAndRoleId(@Param("roleId") Long roleId, @Param("searchKeywords") String searchKeywords, Pageable pageable);

    @Query(value = "SELECT * FROM users "
            + "WHERE name LIKE %:name% ", nativeQuery = true)
    Optional<List<User>> findAllByCustomerName(@Param("name") String name);

    @Query(value = "SELECT * FROM users "
            + "WHERE email like :email "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<List<User>> getUsersByEmail(@Param("email") String email);
}
