package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.Permission;
import vn.viettel.authorization.entities.Role;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface PermissionRepository extends BaseRepository<Permission> {
    @Query(value = "SELECT UNIQUE p.ID FROM PERMISSIONS p JOIN ROLE_PERMISSION_MAP r " +
            "ON p.ID = r.PERMISSION_ID WHERE r.ROLE_ID = :roleId AND p.PERMISSION_TYPE = 2", nativeQuery = true)
    List<BigDecimal> findByRoleId(Long roleId);

    @Query(value = "SELECT UNIQUE * FROM PERMISSIONS p JOIN ROLE_PERMISSION_MAP r " +
            "ON p.ID = r.PERMISSION_ID WHERE r.ROLE_ID IN :roleId AND p.PERMISSION_TYPE IS NOT NULL AND p.status = 1 AND r.status = 1", nativeQuery = true)
    List<Permission> findByListRoleId(List<Long> roleId);


    @Query("Select distinct r from Permission p " +
            "join RolePermission rp on p.id = rp.permissionId " +
            "join Role r on r.id = rp.roleId " +
            "where r.id in (:roleIds) and r.status =1 and p.permissionType is not null and p.status = 1 and rp.status = 1")
    List<Role> findRoles(List<Long> roleIds);
}
