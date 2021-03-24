package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.authorization.RolePermission;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface RolePermissionMapRepository extends BaseRepository<RolePermission> {
    List<RolePermission> findByRoleId(Long roleId);
    List<RolePermission> findByRoleIdAndStatus(Long roleId, Integer status);
}
