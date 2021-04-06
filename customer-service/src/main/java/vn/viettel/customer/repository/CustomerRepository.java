package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> getCustomerByCustomerCodeAndDeletedAtIsNull(String cusCode);

    Optional<Customer> getCustomerByIdNo(String idNo);

    Optional<Customer> getCustomerByPhone(String phone);

    @Query(value = "SELECT * FROM CUSTOMERS WHERE PHONE = :phone OR MOBIPHONE = :phone", nativeQuery = true)
    Customer findByPhoneOrMobiPhone(String phone);

    @Query(value = "SELECT * FROM CUSTOMERS WHERE STATUS = 1 AND ID = :id", nativeQuery = true)
    Customer getCustomerById(Long id);

    List<Customer> getCustomersByShopId(Long shopId);

    @Query(value = "SELECT COUNT(ID) FROM CUSTOMERS WHERE CUSTOMERS.SHOP_ID = :shopId ", nativeQuery = true)
    int getCustomerNumber(@Param("shopId") Long shopId);

    Customer findByIdAndCustomerTypeId(Long id, Long typeId);
}
