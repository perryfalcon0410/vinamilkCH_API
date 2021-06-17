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

    Optional<Customer> getCustomerByMobiPhoneAndStatus(String mobiPhone, Integer status);

    @Query(value = "SELECT * FROM CUSTOMERS WHERE STATUS = 1 AND ID = :shopId", nativeQuery = true)
    List<Customer> getCustomersByShopId(Long shopId);

    @Query(value = "SELECT * FROM customers WHERE SHOP_ID =:shopId AND STATUS = 1 " +
            "            ORDER BY CUSTOMER_CODE DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", nativeQuery = true)
    Customer getCustomerNumber(@Param("shopId") Long shopId);

    @Query(value = "SELECT * FROM CUSTOMERS WHERE SHOP_ID =:shopId AND IS_DEFAULT = 1 "
            + " AND STATUS = 1 ", nativeQuery = true)
    Optional<Customer> getCustomerDefault(Long shopId);

    @Query(value = "SELECT * FROM CUSTOMERS where SHOP_ID = ?1 ORDER BY customer_code ASC", nativeQuery = true)
    List<Customer> findAllDesc(Long shopId);

    @Query(value = "SELECT c.ID FROM CUSTOMERS c where ( c.CUSTOMER_CODE like %:nameOrCode% OR c.NAME_TEXT like %:nameOrCode% ) and c.MOBIPHONE like %:customerPhone% ",
        nativeQuery = true)
    List<Long> getCustomerIds(String nameOrCode, String customerPhone);

}
