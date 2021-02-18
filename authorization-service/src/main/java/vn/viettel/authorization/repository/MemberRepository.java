package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.Member;
import vn.viettel.core.dto.ManagementUserNameDBResponseDTO;
import vn.viettel.core.dto.MemberBookingHistoryDBResponseDTO;
import vn.viettel.core.dto.SalonMemberDBResponseDTO;
import vn.viettel.core.dto.salon.MemberQRDetailDBResponseDTO;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends BaseRepository<Member> {
    @Query(value = "SELECT * FROM members "
            + "WHERE email = :email "
            + "AND company_id = :companyId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<Member> getMemberByEmail(@Param("email") String email, @Param("companyId") Long companyId);

    @Query(value = "SELECT * FROM members "
            + "WHERE member_id = :id "
            + "AND company_id = :companyId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<Member> getMemberById(@Param("id") Long id, @Param("companyId") Long companyId);

    @Query(value = "SELECT * FROM members "
            + "WHERE email = :email "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<Member> getMemberByEmail(@Param("email") String email);

    @Query(value = "SELECT "
            + "     m.member_id AS memberId,"
            + "     m.company_id AS companyId,"
            + "     c.customer_id AS customerId,"
            + "     c.salon_id AS salonId,"
            + "     m.first_name AS firstName,"
            + "     m.last_name AS lastName,"
            + "     m.katakana_first_name AS katakanaFirstName,"
            + "     m.katakana_last_name AS katakanaLastName,"
            + "     m.birthday AS birthday,"
            + "     m.tel AS tel, "
            + "     m.qr_code AS qrCode, "
            + "     m.point AS point "
            + "FROM members AS m "
            + "LEFT JOIN customers AS c ON m.member_id = c.member_id "
            + "WHERE m.qr_code = :qrCode "
            + "AND m.deleted_at IS NULL "
            + "AND c.deleted_at IS NULL "
            + "AND m.status = 1 ", nativeQuery = true)
    MemberQRDetailDBResponseDTO getMemberCustomerRegistrationByQrCode(@Param("qrCode") String qrCode);

    @Query(value = "SELECT "
            + "     m.member_id AS memberId,"
            + "     m.company_id AS companyId,"
            + "     c.customer_id AS customerId,"
            + "     c.salon_id AS salonId,"
            + "     m.first_name AS firstName,"
            + "     m.last_name AS lastName,"
            + "     m.katakana_first_name AS katakanaFirstName,"
            + "     m.katakana_last_name AS katakanaLastName,"
            + "     m.birthday AS birthday,"
            + "     m.tel AS tel, "
            + "     m.qr_code AS qrCode, "
            + "     m.point AS point "
            + "FROM members AS m "
            + "LEFT JOIN customers AS c ON m.member_id = c.member_id "
            + "WHERE m.member_id = :memberId "
            + "AND m.deleted_at IS NULL "
            + "AND c.deleted_at IS NULL "
            + "AND m.status = 1 ", nativeQuery = true)
    MemberQRDetailDBResponseDTO getMemberCustomerRegistrationById(@Param("memberId") Long memberId);

    Member findByQrCodeAndDeletedAtIsNull(String qrCode);

    Optional<List<Member>> getAllByTelAndDeletedAtIsNull(String tel);

    @Query(value = "SELECT m.member_id as memberId, GROUP_CONCAT(sm.salon_id SEPARATOR ',') AS salonIds "
            + "FROM members m "
            + "INNER JOIN salon_members sm ON m.member_id = sm.member_id "
            + "WHERE m.member_id = :memberId "
            + "GROUP BY m.member_id "
            + "AND sm.deleted_at IS NULL "
            + "AND m.deleted_at IS NULL", nativeQuery = true)
    SalonMemberDBResponseDTO getSalonMember(@Param("memberId") Long memberId);

    @Query(value = "SELECT * " +
            "FROM members m1 " +
            "WHERE m1.member_id = :fromMemberId " +
            "UNION " +
            "SELECT * " +
            "FROM members m2 " +
            "WHERE m2.member_id = :toMemberId", nativeQuery = true)
    List<Member> getFromAndToMember(@Param("fromMemberId") Long memberId, @Param("toMemberId") Long toMemberId);

    @Query(value = "SELECT m.member_id as memberId, GROUP_CONCAT(c.customer_id SEPARATOR ',') as customerId "
            + "FROM members m "
            + "JOIN customers c ON m.member_id = c.member_id "
            + "WHERE m.member_id = :memberId "
            + "AND m.deleted_at IS NULL "
            + "AND c.deleted_at IS NULL "
            + "GROUP BY m.member_id", nativeQuery = true)
    MemberBookingHistoryDBResponseDTO getBookingHistoryByMember(@Param("memberId") Long memberId);

    @Query(value = "SELECT management_user_id AS managementUserId, name " +
            "FROM management_users " +
            "WHERE management_user_id IN :managementIds " +
            "AND deleted_at IS NULL", nativeQuery = true)
    List<ManagementUserNameDBResponseDTO> getManagementUserNameById(@Param("managementIds") List<Long> managementIds);

    String sqlQueryPGetMemberAndCustomer = "pGetMemberAndCustomer";

    String sqlCountQueryPGetMemberAndCustomer = "pCountGetMemberAndCustomer";

    @Query(value = "SELECT * FROM members "
            + "WHERE tel = :tel "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Member getMemberByTelAndDeletedAtIsNull (@Param("tel") String tel);

}
