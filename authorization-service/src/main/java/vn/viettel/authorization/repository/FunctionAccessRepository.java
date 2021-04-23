package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.FunctionAccess;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface FunctionAccessRepository extends BaseRepository<FunctionAccess> {
    List<FunctionAccess> findByPermissionId(Long permissionId);

    @Query(value = "SELECT UNIQUE * FROM FUNCTION_ACCESS f join PERMISSIONS p on f.PERMISSION_ID = p.ID " +
            "JOIN ROLE_PERMISSION_MAP r on r.PERMISSION_ID = p.ID WHERE r.ROLE_ID = :roleId", nativeQuery = true)
    List<FunctionAccess> findByRoleId(Long roleId);
}
