package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.dto.MemberCustomerInfoDBResponseDTO;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer> {
    @Procedure(procedureName = "update_customer_qrcode")
    String updateCustomerQrCode(@Param("idc") Long salonId);

    Optional<Customer> findByQrCodeAndDeletedAtIsNull(String qrCode);

    Optional<List<Customer>> findByTelAndDeletedAtIsNull(String tel);

    Optional<List<Customer>> findAllByMemberIdAndDeletedAtIsNull(Long memberId);

    List<Customer> findByIdInAndDeletedAtIsNull(List<Long> customerIds);

    @Query(value = "SELECT " +
            "    m.member_id as memberId, " +
            "    m.first_name as memberFirstName, " +
            "    m.last_name as memberLastName, " +
            "    m.katakana_first_name as memberKatakanaFirstName, " +
            "    m.katakana_last_name as memberKatakanaLastName, " +
            "    m.company_id as companyId, " +
            "    m.email as memberEmail, " +
            "    CONVERT (m.gender, UNSIGNED INTEGER) as memberGender, " +
            "    m.birthday as memberBirthday, " +
            "    m.tel as memberTel, " +
            "    m.qr_code as memberQrCode, " +
            "    c.customer_id as customerId, " +
            "    c.first_name as customerFirstName, " +
            "    c.last_name as customerLastName, " +
            "    c.katakana_first_name as customerKatakanaFirstName, " +
            "    c.katakana_last_name as customerKatakanaLastName, " +
            "    c.salon_id as salonId, " +
            "    CONVERT (c.gender, UNSIGNED INTEGER) as customerGender, " +
            "    c.birthday as customerBirthday, " +
            "    c.tel as customerTel, " +
            "    c.qr_code as customerQrCode " +
            "FROM members m " +
            "INNER JOIN customers c ON c.member_id = m.member_id " +
            "WHERE c.deleted_at IS NULL " +
            "AND m.deleted_at IS NULL " +
            "AND m.status = 1 " +
            "AND c.customer_id = :customerId", nativeQuery = true)
    MemberCustomerInfoDBResponseDTO getMemberCustomerRegistrationInfo(@Param("customerId") Long customerId);

    @Query(value = "SELECT " +
            "    m.member_id as memberId, " +
            "    m.first_name as memberFirstName, " +
            "    m.last_name as memberLastName, " +
            "    m.katakana_first_name as memberKatakanaFirstName, " +
            "    m.katakana_last_name as memberKatakanaLastName, " +
            "    m.company_id as companyId, " +
            "    m.email as memberEmail, " +
            "    CONVERT (m.gender, UNSIGNED INTEGER) as memberGender, " +
            "    m.birthday as memberBirthday, " +
            "    m.tel as memberTel, " +
            "    m.qr_code as memberQrCode, " +
            "    c.customer_id as customerId, " +
            "    c.first_name as customerFirstName, " +
            "    c.last_name as customerLastName, " +
            "    c.katakana_first_name as customerKatakanaFirstName, " +
            "    c.katakana_last_name as customerKatakanaLastName, " +
            "    c.salon_id as salonId, " +
            "    CONVERT (c.gender, UNSIGNED INTEGER) as customerGender, " +
            "    c.birthday as customerBirthday, " +
            "    c.tel as customerTel, " +
            "    c.qr_code as customerQrCode " +
            "FROM members m " +
            "INNER JOIN customers c ON c.member_id = m.member_id " +
            "WHERE c.deleted_at IS NULL " +
            "AND m.deleted_at IS NULL " +
            "AND m.status = 1 " +
            "AND c.customer_id IN :customerIds", nativeQuery = true)
    List<MemberCustomerInfoDBResponseDTO> getMemberCustomerRegistrationInfoList(@Param("customerIds") List<Long> customerId);

    @Query(value = "SELECT * FROM customers " +
            "WHERE tel = :tel AND deleted_at IS NULL ", nativeQuery = true)
    Customer getCustomerByTelAndDeletedAtIsNotNull(String tel);

}
