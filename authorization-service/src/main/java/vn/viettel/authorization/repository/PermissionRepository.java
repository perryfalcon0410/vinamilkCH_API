package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.Permission;
import vn.viettel.authorization.entities.Role;
import vn.viettel.authorization.entities.Shop;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PermissionRepository extends BaseRepository<Permission> {

    @Query(value = "SELECT distinct p.id FROM Permission p JOIN RolePermission r " +
            "ON p.id = r.permissionId WHERE r.roleId = :roleId AND p.permissionType = 2")
    List<Long> findByRoleId(Long roleId);

    //Các roles - shop đang hoạt động có quyền dữ liệu
    @Query("Select distinct r from Permission p " +
            "join RolePermission rp on p.id = rp.permissionId " +
            "join Role r on r.id = rp.roleId " +
            "join OrgAccess org on p.id = org.permissionId " +
            "join Shop s on s.id = org.shopId " +
            "Where p.permissionType = 2 And r.id In (:roleIds)  " +
            " And p.status = 1 And rp.status = 1 and r.status =1 And org.status =1 And s.status = 1 ")
    List<Role> findRoles(List<Long> roleIds);

    //Các các shop có quyền dữ liệu theo role
    @Query("Select distinct s from Permission p " +
            "join RolePermission rp on p.id = rp.permissionId " +
            "join Role r on r.id = rp.roleId " +
            "join OrgAccess org on p.id = org.permissionId " +
            "join Shop s on s.id = org.shopId " +
            "Where p.permissionType = 2 And r.id =:roleId " +
            " And p.status = 1 And rp.status = 1 and r.status =1 And org.status =1 And s.status = 1 ")
    List<Shop> findShops(Long roleId);

    //Lấy các permistion của shop - role theo type
    @Query("Select distinct p from Permission p " +
            "join RolePermission rp on p.id = rp.permissionId " +
            "join Role r on r.id = rp.roleId " +
            "join OrgAccess org on p.id = org.permissionId " +
            "join Shop s on s.id = org.shopId " +
            "where r.id =:roleId and org.shopId =:shopId  and p.permissionType =:type " +
            "and r.status =1 and org.status =1 and s.status = 1 and p.status = 1 and rp.status = 1")
    List<Permission> findPermissionType(Long roleId, Long shopId, Integer type);


    //Lấy các permistion của shop - role type(1,3)
    @Query("Select distinct p from Permission p " +
            "join RolePermission rp on p.id = rp.permissionId " +
            "join Role r on r.id = rp.roleId " +
            "where r.id =:roleId  and p.permissionType In (1, 3)" +
            "and r.status = 1 and p.status = 1 and rp.status = 1")
    List<Permission> findPermissionByRole(Long roleId);

}
