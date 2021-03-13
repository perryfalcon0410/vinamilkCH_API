package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.Permission;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface PermissionRepository extends BaseRepository<Permission> {

    @Query(value = "SELECT DISTINCT FUNCTION_ID FROM PERMISSIONS WHERE ROLE_ID IN :ids", nativeQuery = true)
    List<BigDecimal> getFunctionIdByRoles(List<Integer> ids);

    @Query(value = "SELECT DISTINCT ACTION_ID FROM PERMISSIONS " +
            "WHERE ROLE_ID IN :roleIds AND FUNCTION_ID = :funcId", nativeQuery = true)
    List<BigDecimal> getActionsAllow(List<Integer> roleIds, int funcId);
}
