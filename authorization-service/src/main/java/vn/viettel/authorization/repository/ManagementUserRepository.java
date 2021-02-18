package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.ManagementUsers;
import vn.viettel.core.dto.salon.SalonHairdresserDBResponseDTO;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ManagementUserRepository extends BaseRepository<ManagementUsers> {

    @Query(value = "SELECT * FROM management_users "
        + "WHERE object = :object "
        + "AND object_id = :objectId "
        + "AND role_id = :roleId "
        + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<ManagementUsers> getUserByObjectAndObjectIdAndRoleId(
        @Param("object") int object,
        @Param("objectId") Long objectId,
        @Param("roleId") Long roleId);

    @Query(value = "SELECT * FROM management_users "
        + "WHERE email like :email "
        + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<ManagementUsers> getUserByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM management_users "
            + "WHERE email = :email "
            + "AND company_id = :companyId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<ManagementUsers> getManagementUserByEmailAndCompanyId(@Param("email") String email, @Param("companyId") Long companyId);

    @Query(value = "SELECT * FROM management_users "
            + "WHERE management_user_id = :managementUserId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<ManagementUsers> getManagementUserById(@Param("managementUserId") Long managementUserId);

    @Query(value = "SELECT * FROM management_users "
            + "WHERE (email like %:searchKeywords% or name like %:searchKeywords%) "
            + "AND company_id = :idCompany "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Page<ManagementUsers> getUserManagementIndexBySearchKeywords(@Param("searchKeywords") String searchKeywords,@Param("idCompany") Long idCompany, Pageable pageable);

    @Query(value = "SELECT management_users.* FROM management_users "
            + "INNER JOIN privileges_users ON privileges_users.management_user_id = management_users.management_user_id "
            + "WHERE management_users.company_id = :companyId "
            + "AND privileges_users.management_privilege_id = 1 "
            + "AND management_users.deleted_at IS NULL", nativeQuery = true)
    ManagementUsers getUserAdminByCompanyId(@Param("companyId") Long companyId);

    @Query(value = "" +
            "SELECT " +
            "   mu.management_user_id as id, " +
            "   mu.name AS name, " +
            "   mp.privilege_name, " +
            "   mu.description, " +
            "   mu.beautician_cost, " +
            "   mu.photo_url " +
            "FROM management_users mu " +
            "INNER JOIN privileges_users pu ON mu.management_user_id = pu.management_user_id " +
            "INNER JOIN management_privileges mp ON pu.management_privilege_id = mp.management_privilege_id " +
            "WHERE mu.salon_id = :salonId " +
            "   AND mp.management_privilege_id IN :privilegeIds " +
            "   AND mu.deleted_at IS NULL " +
            "   AND pu.deleted_at IS NULL " +
            "   AND mp.deleted_at IS NULL " +
            "ORDER BY id", nativeQuery = true)
    List<SalonHairdresserDBResponseDTO> findAllHairdressersInSalonAtNullDeleted(@Param("salonId") Long salonId,
            @Param("privilegeIds") List<Long> privilegeIds);

    List<ManagementUsers> findAllByIdInAndDeletedAtIsNull(List<Long> ids);
}
