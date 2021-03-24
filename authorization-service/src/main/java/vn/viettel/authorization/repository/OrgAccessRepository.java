package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.authorization.OrgAccess;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;

public interface OrgAccessRepository extends BaseRepository<OrgAccess> {
    OrgAccess findByPermissionId(Long permissionId);

    @Query(value = "SELECT SHOP_ID FROM ORG_ACCESS WHERE PERMISSION_ID = :permissionId", nativeQuery = true)
    BigDecimal finShopIdByPermissionId(Long permissionId);
}
