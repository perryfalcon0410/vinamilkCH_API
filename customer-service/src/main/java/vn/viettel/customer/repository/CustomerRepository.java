package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.customer.entities.Customer;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> getCustomerByIdNo(String idNo);

    Optional<Customer> getCustomerByPhone(String phone);

    @Query(value = "SELECT * FROM CUSTOMERS WHERE (PHONE =:phone OR MOBIPHONE =:phone) "
            + "AND DELETED_AT IS NULL", nativeQuery = true)
    Customer findByPhoneOrMobiPhone(String phone);

    @Query(value = "SELECT * FROM CUSTOMERS WHERE STATUS = 1 AND ID = :shopId", nativeQuery = true)
    List<Customer> getCustomersByShopId(Long shopId);

    @Query(value = "SELECT COUNT(ID) FROM CUSTOMERS WHERE CUSTOMERS.SHOP_ID = :shopId ", nativeQuery = true)
    int getCustomerNumber(@Param("shopId") Long shopId);

    @Query(value = "SELECT * FROM CUSTOMERS WHERE SHOP_ID =:shopId AND IS_DEFAULT = 1 "
            + " AND STATUS = 1 AND DELETED_AT IS NULL", nativeQuery = true)
    Optional<Customer> getCustomerDefault(Long shopId);
    @Query(value = "SELECT * FROM CUSTOMERS ORDER BY customer_code ASC", nativeQuery = true)
    List<Customer> findAllDesc();
}
