package vn.viettel.authorization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.viettel.core.db.entity.PasswordReset;
import vn.viettel.core.repository.BaseRepository;

public interface PasswordResetRepository extends BaseRepository<PasswordReset> {

    /* GET BY USER ID */
    @Query(value = "SELECT * FROM password_resets "
        + "WHERE user_id = :userId "
        + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<PasswordReset> getByUserId(@Param("userId") Long userId);

    /* GET BY ADMIN USER ID */
    @Query(value = "SELECT * FROM password_resets "
            + "WHERE admin_user_id = :userId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<PasswordReset> getByAdminUserId(@Param("userId") Long userId);

    /* GET BY Member ID */
    @Query(value = "SELECT * FROM password_resets "
            + "WHERE member_id = :userId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<PasswordReset> getByMemberId(@Param("userId") Long userId);

    /* GET BY TOKEN */
    @Query(value = "SELECT * FROM password_resets "
        + "WHERE token like :token "
        + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<PasswordReset> getByToken(@Param("token") String token);

    @Query(value = "SELECT * FROM password_resets "
            + "WHERE management_user_id = :managementUserId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<PasswordReset> getByManagementUserId(@Param("managementUserId") Long managementUserId);

    @Query(value = "SELECT * FROM password_resets "
            + "WHERE member_id = :userId "
            + "AND company_id = :companyId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<PasswordReset> getByMemberIdAndCompanyId(@Param("userId") Long userId, @Param("companyId") Long companyId);

    @Query(value = "SELECT * FROM password_resets "
            + "WHERE token like :token "
            + "AND company_id = :companyId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<PasswordReset> getByTokenAndCompanyId(@Param("token") String token,  @Param("companyId") Long companyId);

    @Query(value = "SELECT * FROM password_resets "
            + "WHERE member_id = :memberId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    List<PasswordReset> getByMember(@Param("memberId") Long memberId);
}
