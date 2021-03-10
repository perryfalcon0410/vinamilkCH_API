package vn.viettel.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigInteger;
import java.util.List;

public interface CustomerRepository extends BaseRepository<Customer> {
    @Query(value = "SELECT * FROM customers WHERE deleted_at is NULL ", nativeQuery = true)
    Page<Customer> findAll(Pageable pageable);

    @Query(value = "SELECT COUNT(id) FROM customers", nativeQuery = true)
    int getCustomerNumber();

    @Query(value = "SELECT * FROM customers " +
            "where cus_type = :type " +
            "and deleted_at is NULL", nativeQuery = true)
    List<Customer> findCustomerByType(int type);

    /* START QUERIES FOR SEARCH CUSTOMER */
    // search by phone number
    Customer findByPhoneNumber(String phoneNumber);
    // search by identity number
    @Query(value = "SELECT c.id FROM customers c join id_cards i " +
            "on c.identity_card_id = i.id " +
            "where i.id_no = :idNumber " +
            "and c.deleted_at is NULL", nativeQuery = true)
    BigInteger findCustomerIdByIDNumber(String idNumber);
    // search by name or code
    @Query(value = "SELECT id FROM customers " +
            "where (concat(first_name, last_name) like %:name% " +
            "or cus_code = :code) " +
            "and deleted_at is NULL", nativeQuery = true)
    List<BigInteger> findCustomerIdByNameOrCode(@Param("name") String name, String code);
    // search by name/code and phone number
    @Query(value = "SELECT id FROM customers " +
            "where (concat(first_name, last_name) like %:name% " +
            "or cus_code = :code) " +
            "and phone_number = :phone " +
            "and deleted_at is NULL", nativeQuery = true)
    BigInteger findCustomerIdByNameOrCodeAndPhoneNumber(@Param("name") String name, String code, String phone);
    // search by name/code and identity number
    @Query(value = "SELECT c.id FROM customers c join id_cards i " +
            "on c.identity_card_id = i.id " +
            "where i.id_no = :idNumber " +
            "and (concat(c.first_name, c.last_name) like %:name% " +
            "or c.cus_code =:code) " +
            "and c.deleted_at is NULL", nativeQuery = true)
    BigInteger findCustomerIdByIDNumberAndNameOrCode(String idNumber, @Param("name") String name, String code);
    // search by phone number and identity number
    @Query(value = "SELECT c.id FROM customers c join id_cards i " +
            "on c.identity_card_id = i.id " +
            "where i.id_no = :idNumber " +
            "and c.phone_number = :phone " +
            "and c.deleted_at is NULL", nativeQuery = true)
    BigInteger findCustomerByPhoneNUmberAndIDNumber(String phone, String idNumber);
    // search by all fields
    @Query(value = "SELECT c.id FROM customers c join id_cards i " +
            "on c.identity_card_id = i.id " +
            "where i.id_no = :idNumber " +
            "and c.phone_number = :phone " +
            "and (concat(c.first_name, c.last_name) like %:name% " +
            "or c.cus_code =:code) " +
            "and c.deleted_at is NULL", nativeQuery = true)
    BigInteger findCustomerByAllField(@Param("name") String name, String code, String phone, String idNumber);
    /* END QUERIES FOR SEARCH CUSTOMER */

}
