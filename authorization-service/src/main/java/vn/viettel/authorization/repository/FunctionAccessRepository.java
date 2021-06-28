package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.FunctionAccess;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Set;

public interface FunctionAccessRepository extends BaseRepository<FunctionAccess> {
    List<FunctionAccess> findByPermissionId(Long permissionId);

    @Query(value = "SELECT UNIQUE * FROM FUNCTION_ACCESS f join PERMISSIONS p on f.PERMISSION_ID = p.ID " +
            "JOIN ROLE_PERMISSION_MAP r on r.PERMISSION_ID = p.ID WHERE r.ROLE_ID = :roleId " +
            " AND p.PERMISSION_TYPE = 1 AND f.status = 1 AND p. status = 1 AND r.status = 1", nativeQuery = true)
    List<FunctionAccess> findByRoleId(Long roleId);

    List<FunctionAccess> findByPermissionIdInAndStatus(Set<Long> permisionIds, Integer status);
}
