package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.ManagementPrivilege;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ManagementPrivilegeRepository  extends BaseRepository<ManagementPrivilege> {
    @Query(value = "SELECT * FROM management_privileges "
            + "WHERE management_privilege_id = :id "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<ManagementPrivilege> getById(@Param("id") Long id);
}
