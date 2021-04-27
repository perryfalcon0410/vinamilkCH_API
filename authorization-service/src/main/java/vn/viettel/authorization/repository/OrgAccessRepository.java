package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.OrgAccess;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface OrgAccessRepository extends BaseRepository<OrgAccess> {
    OrgAccess findByPermissionId(Long permissionId);

    @Query(value = "SELECT UNIQUE o.SHOP_ID FROM ORG_ACCESS o JOIN ROLE_PERMISSION_MAP r " +
            "ON o.PERMISSION_ID = r.PERMISSION_ID WHERE r.ROLE_ID = :roleId AND o.status = 1 AND r.status = 1", nativeQuery = true)
    List<BigDecimal> finShopIdByRoleId(Long roleId);
}
