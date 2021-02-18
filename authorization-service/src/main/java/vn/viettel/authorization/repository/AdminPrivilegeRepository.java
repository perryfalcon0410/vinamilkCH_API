package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.AdminPrivilege;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminPrivilegeRepository extends BaseRepository<AdminPrivilege> {
    @Query(value = "SELECT * FROM service_admin_privileges "
            + "WHERE service_admin_privilege_id = :id "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<AdminPrivilege> getAdminPrivilegeById(@Param("id") Long id);
}
