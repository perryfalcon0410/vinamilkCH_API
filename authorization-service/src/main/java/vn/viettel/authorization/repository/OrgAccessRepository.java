package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.OrgAccess;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface OrgAccessRepository extends BaseRepository<OrgAccess> {

//    @Query(value = "SELECT DISTINCT o.SHOP_ID FROM ORG_ACCESS o JOIN ROLE_PERMISSION_MAP r " +
//            "ON o.PERMISSION_ID = r.PERMISSION_ID WHERE r.ROLE_ID = :roleId AND o.status = 1 AND r.status = 1", nativeQuery = true)
//    List<BigDecimal> finShopIdByRoleId(Long roleId);

    @Query(value = "SELECT SHOP_ID FROM ORG_ACCESS WHERE PERMISSION_ID IN :perIds", nativeQuery = true)
    List<BigDecimal> findShopIdByPermissionId(List<BigDecimal> perIds);


    @Query("select distinct o.shopId From OrgAccess o join RolePermission r on o.permissionId = r.permissionId " +
            "join Shop s on s.id = o.shopId " +
            "Where s.status = 1 " +
            "   and r.roleId = :roleId and o.status = 1 and r.status = 1")
    List<Long> finShopIdByRoleId(Long roleId);
}
