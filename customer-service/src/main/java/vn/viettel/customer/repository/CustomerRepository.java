package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.customer.entities.Customer;
import vn.viettel.core.repository.BaseRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> getCustomerByIdNo(String idNo);
    @Query(value = "SELECT c FROM Customer c WHERE c.mobiPhone like %:mobiPhone AND c.status =:status ")
    List<Customer> getCustomerByMobiPhoneAndStatus(String mobiPhone, Integer status);

    List<Customer> getAllByMobiPhoneAndStatus(String mobiPhone, Integer status);

    @Query(value = "SELECT c FROM Customer c WHERE (:status IS NULL OR c.status = :status) AND c.id IN (:customerIds)")
    List<Customer> getCustomerInfo(Integer status, List<Long> customerIds);

    @Query(value = "SELECT c FROM Customer c WHERE c.shopId =:shopId " +
            " AND c.customerCode NOT LIKE '%.KA___' " +
            " AND c.id = (SELECT MAX (cus.id) FROM Customer cus WHERE cus.shopId =:shopId AND cus.customerCode NOT LIKE '%.KA___' ) " +
            " ORDER BY c.id desc , c.createdAt desc ")
    List<Customer> getCustomerNumber(@Param("shopId") Long shopId);

    @Query(value = "SELECT c FROM Customer c WHERE c.shopId =:shopId AND c.isDefault = true "
            + " AND c.status = 1 ORDER BY c.updatedAt DESC")
    List<Customer> getCustomerDefault(Long shopId);

    @Query(value = "SELECT c.ID FROM CUSTOMERS c where ( c.CUSTOMER_CODE like %:nameOrCode% OR c.NAME_TEXT like %:nameOrCode% ) and c.MOBIPHONE like %:customerPhone ",
            nativeQuery = true)
    List<Long> getCustomerIds(String nameOrCode, String customerPhone);


    @Modifying()
    @Query(value = "Update Customer SET dayOrderNumber = 0 , dayOrderAmount = 0 ")
    int schedulerUpdateStartDay();

    @Modifying()
    @Query(value = "Update Customer SET dayOrderNumber = 0 , dayOrderAmount = 0, monthOrderNumber = 0 , monthOrderAmount = 0 ")
    int schedulerUpdateStartMonth();

}
