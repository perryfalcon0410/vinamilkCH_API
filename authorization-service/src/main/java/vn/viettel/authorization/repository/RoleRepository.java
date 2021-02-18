package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.Role;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role> {

    @Query(value = "SELECT * FROM roles "
        + "WHERE role_name like :roleName "
        + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<Role> findByRoleName(@Param("roleName") String roleName);

    @Query(value = "SELECT * FROM roles "
        + "WHERE id = :id "
        + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<Role> getById(@Param("id") Long id);

    List<Role> findAllByDeletedAtIsNull();
}
