package vn.viettel.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerRepository extends BaseRepository<Customer> {
    @Query(value = "SELECT * FROM CUSTOMERS WHERE DELETED_AT IS NULL ", nativeQuery = true)
    Page<Customer> findAll(Pageable pageable);

    @Query(value = "SELECT COUNT(id) FROM CUSTOMERS", nativeQuery = true)
    int getCustomerNumber();

    @Query(value = "SELECT * FROM CUSTOMERS " +
            "WHERE CUS_TYPE = :type " +
            "and DELETED_AT IS NULL", nativeQuery = true)
    List<Customer> findCustomerByType(int type);

    /* START QUERIES FOR SEARCH CUSTOMER */
    // search by phone number
    @Query(value = "SELECT * FROM CUSTOMERS WHERE PHONE_NUMBER = :phoneNumber", nativeQuery = true)
    Customer findByPhoneNumber(String phoneNumber);
    // search by identity number
    @Query(value = "SELECT c.ID FROM CUSTOMERS c join ID_CARDS i " +
            "on c.IDENTITY_CARD_ID = i.id " +
            "where i.ID_NO = :idNumber " +
            "and c.DELETED_AT IS NULL", nativeQuery = true)
    BigDecimal findCustomerIdByIDNumber(String idNumber);
    // search by name or code
    @Query(value = "SELECT ID FROM CUSTOMERS " +
            "where (CONCAT(FIRST_NAME, LAST_NAME) LIKE %:name% " +
            "or CUS_CODE = :code) " +
            "and DELETED_AT is NULL", nativeQuery = true)
    List<BigDecimal> findCustomerIdByNameOrCode(@Param("name") String name, String code);
    // search by name/code and phone number
    @Query(value = "SELECT ID FROM CUSTOMERS " +
            "where (CONCAT(FIRST_NAME, LAST_NAME) like %:name% " +
            "or CUS_CODE = :code) " +
            "and PHONE_NUMBER = :phone " +
            "and DELETED_AT is NULL", nativeQuery = true)
    BigDecimal findCustomerIdByNameOrCodeAndPhoneNumber(@Param("name") String name, String code, String phone);
    // search by name/code and identity number
    @Query(value = "SELECT c.ID FROM CUSTOMERS c join ID_CARDS i " +
            "on c.IDENTITY_CARD_ID = i.id " +
            "where i.ID_NO = :idNumber " +
            "and (CONCAT(FIRST_NAME, LAST_NAME) like %:name% " +
            "or c.CUS_CODE =:code) " +
            "and c.DELETED_AT is NULL", nativeQuery = true)
    BigDecimal findCustomerIdByIDNumberAndNameOrCode(String idNumber, @Param("name") String name, String code);
    // search by phone number and identity number
    @Query(value = "SELECT c.ID FROM CUSTOMERS c join ID_CARDS i " +
            "on c.IDENTITY_CARD_ID = i.id " +
            "where i.ID_NO = :idNumber " +
            "and c.PHONE_NUMBER = :phone " +
            "and c.DELETED_AT is NULL", nativeQuery = true)
    BigDecimal findCustomerByPhoneNUmberAndIDNumber(String phone, String idNumber);
    // search by all fields
    @Query(value = "SELECT c.ID FROM CUSTOMERS c join ID_CARDS i " +
            "on c.IDENTITY_CARD_ID = i.id " +
            "where i.ID_NO = :idNumber " +
            "and c.PHONE_NUMBER = :phone " +
            "and (CONCAT(FIRST_NAME, LAST_NAME) like %:name% " +
            "or c.CUS_CODE =:code) " +
            "and c.DELETED_AT is NULL", nativeQuery = true)
    BigDecimal findCustomerByAllField(@Param("name") String name, String code, String phone, String idNumber);
    /* END QUERIES FOR SEARCH CUSTOMER */

}
