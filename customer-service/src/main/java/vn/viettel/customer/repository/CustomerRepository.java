package vn.viettel.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import vn.viettel.customer.entities.Customer;
import vn.viettel.core.repository.BaseRepository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> getCustomerByIdNo(String idNo);

    @Query(value = "SELECT c FROM Customer c WHERE c.mobiPhone like %:mobiPhone AND c.status =:status ")
    List<Customer> getCustomerByMobiPhoneAndStatus(String mobiPhone, Integer status);

    List<Customer> getAllByMobiPhoneAndStatus(String mobiPhone, Integer status);

    @Query(value = "SELECT c FROM Customer c WHERE (:status IS NULL OR c.status = :status) AND c.id IN (:customerIds)")
    List<Customer> getCustomerInfo(Integer status, List<Long> customerIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE )
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
    @Query(value = "SELECT c FROM Customer c WHERE c.shopId =:shopId AND c.customerCode NOT LIKE '%.KA___' ORDER BY c.customerCode desc ")
    Page<Customer> getLastCustomerNumber(@Param("shopId") Long shopId,  Pageable pageable);

    @Query(value = "SELECT c FROM Customer c WHERE c.shopId =:shopId AND c.isDefault = true "
            + " AND c.status = 1 ORDER BY c.updatedAt DESC")
    List<Customer> getCustomerDefault(Long shopId);

    @Query(value = "SELECT c.id FROM Customer c where ( c.customerCode like %:nameOrCode% OR c.nameText like %:nameOrCode% ) " +
            "and c.mobiPhone like %:customerPhone ")
    List<Long> getCustomerIds(String nameOrCode, String customerPhone);

    @Modifying()
    @Query(value = "Update Customer SET dayOrderNumber = 0 , dayOrderAmount = 0 ")
    int schedulerUpdateStartDay();

    @Modifying()
    @Query(value = "Update Customer SET dayOrderNumber = 0 , dayOrderAmount = 0, monthOrderNumber = 0 , monthOrderAmount = 0 ")
    int schedulerUpdateStartMonth();

}
