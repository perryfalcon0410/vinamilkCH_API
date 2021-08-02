package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.OrgAccess;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface OrgAccessRepository extends BaseRepository<OrgAccess> {

    @Query(value = "SELECT oa.shopId FROM OrgAccess oa WHERE oa.permissionId IN :perIds")
    List<Long> findShopIdByPermissionId(List<Long> perIds);

    @Query("select distinct o.shopId From OrgAccess o join RolePermission r on o.permissionId = r.permissionId " +
            "join Shop s on s.id = o.shopId " +
            "Where s.status = 1 " +
            "   and r.roleId = :roleId and o.status = 1 and r.status = 1")
    List<Long> finShopIdByRoleId(Long roleId);
}
