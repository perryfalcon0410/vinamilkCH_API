package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.Permission;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface PermissionRepository extends BaseRepository<Permission> {
    @Query(value = "SELECT UNIQUE p.ID FROM PERMISSIONS p JOIN ROLE_PERMISSION_MAP r " +
            "ON p.ID = r.PERMISSION_ID WHERE r.ROLE_ID = :roleId AND p.PERMISSION_TYPE = 2", nativeQuery = true)
    List<BigDecimal> findByRoleId(Long roleId);

    @Query(value = "SELECT UNIQUE * FROM PERMISSIONS p JOIN ROLE_PERMISSION_MAP r " +
            "ON p.ID = r.PERMISSION_ID WHERE r.ROLE_ID IN :roleId AND p.status = 1 AND r.status = 1", nativeQuery = true)
    List<Permission> findByListRoleId(List<Long> roleId);
}
