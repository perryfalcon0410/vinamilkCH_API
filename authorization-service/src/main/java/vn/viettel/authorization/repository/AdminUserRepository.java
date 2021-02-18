package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.AdminUser;
import vn.viettel.core.dto.user.AdminUserDBResponseDTO;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminUserRepository extends BaseRepository<AdminUser> {
    @Query(value = "SELECT * FROM service_admin_users "
            + "WHERE email = :email "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<AdminUser> getAdminUserByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM service_admin_users "
            + "WHERE service_admin_user_id = :id "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<AdminUser> getAdminUserById(@Param("id") Long id);

    @Query(value = "SELECT * FROM service_admin_users "
            + "WHERE (email like %:searchKeywords% or name like %:searchKeywords%) "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Page<AdminUser> getUserAdminIndexBySearchKeywords(@Param("searchKeywords") String searchKeywords, Pageable pageable);

    @Query(value = "SELECT sau.service_admin_user_id as serviceAdminUserId, "
            + "            sau.name as serviceAdminUserName "
            + "FROM service_admin_users sau "
            + "WHERE sau.service_admin_user_id = :id "
            + "AND sau.deleted_at IS NULL", nativeQuery = true)
    AdminUserDBResponseDTO getAdminById(@Param("id") Long id);
}
