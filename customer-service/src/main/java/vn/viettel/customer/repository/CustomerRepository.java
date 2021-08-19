package vn.viettel.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import vn.viettel.core.dto.customer.CustomerDTO;
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

    List<Customer> getAllByMobiPhoneAndStatus(String mobiPhone, Integer status);

    @Query(value = "SELECT c FROM Customer c WHERE (:status IS NULL OR c.status = :status) AND c.id IN (:customerIds)")
    List<Customer> getCustomerInfo(Integer status, List<Long> customerIds);

    @Query(value = "SELECT c FROM Customer c WHERE c.id IN (:customerIds)")
    List<Customer> getCustomersByIds(List<Long> customerIds);

    @Query(value = "SELECT c FROM Customer c WHERE c.shopId =:shopId AND c.customerCode NOT LIKE '%.KA___' ORDER BY c.customerCode desc ")
    Page<Customer> getLastCustomerNumber(@Param("shopId") Long shopId,  Pageable pageable);

    @Query(value = "SELECT c FROM Customer c WHERE c.shopId =:shopId AND c.isDefault = true "
            + " AND c.status = 1 ORDER BY c.updatedAt DESC")
    List<Customer> getCustomerDefault(Long shopId);

    @Query(value = "SELECT c.id FROM Customer c where ( c.customerCode like %:nameOrCode% OR c.nameText like %:nameOrCode% ) " +
            "and c.mobiPhone like %:customerPhone ")
    List<Long> getCustomerIds(String nameOrCode, String customerPhone);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerDTO(c.id, c.firstName, c.lastName, c.customerCode, c.mobiPhone," +
            " c.customerTypeId, c.street, c.address, c.shopId, c.totalBill) " +
            " FROM Customer c WHERE c.status = 1 AND ( :shopId IS NULL OR c.shopId =:shopId )" +
            " AND ( c.nameText like %:nameCodeAddress% OR upper(c.customerCode) like %:nameCodeAddress% " +
            "   OR c.phone like %:phone OR c.mobiPhone like %:phone OR c.addressText like %:nameCodeAddress% ) " +
            " ORDER BY c.customerCode, c.nameText")
    Page<CustomerDTO> searchForSale(Long shopId, String nameCodeAddress, String phone, Pageable pageable);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerDTO(c.id, c.firstName, c.lastName, c.customerCode, c.mobiPhone," +
            " c.customerTypeId, c.street, c.address, c.shopId, c.totalBill) " +
            " FROM Customer c WHERE c.status = 1 AND ( :shopId IS NULL OR c.shopId =:shopId )" +
            " AND ( c.phone like %:phone OR c.mobiPhone like %:phone ) " +
            " ORDER BY c.customerCode, c.nameText")
    Page<CustomerDTO> searchForSaleFone(Long shopId, String phone, Pageable pageable);



    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerDTO(c.id,  c.customerCode, c.firstName, c.lastName, c.mobiPhone," +
            " c.customerTypeId, c.workingOffice, c.officeAddress, c.taxCode ) " +
            " FROM Customer c WHERE c.status = 1 " +
            " AND ( c.nameText like %:searchKeywords% OR upper(c.customerCode) like %:searchKeywords%) " +
            "   AND (c.phone like %:mobiphone OR c.mobiPhone like %:mobiphone) " +
            "   AND c.workingOfficeText like %:workingOffice%  " +
            "   AND c.officeAddressText like %:officeAddress%  " +
            "   AND upper(c.taxCode) like %:taxCode%  " +
            " ORDER BY c.customerCode, c.nameText")
    Page<CustomerDTO> searchForRedInvoice(String searchKeywords, String mobiphone, String workingOffice, String officeAddress, String taxCode, Pageable pageable);


    @Modifying()
    @Query(value = "Update Customer SET dayOrderNumber = 0 , dayOrderAmount = 0 ")
    int schedulerUpdateStartDay();

    @Modifying()
    @Query(value = "Update Customer SET dayOrderNumber = 0 , dayOrderAmount = 0, monthOrderNumber = 0 , monthOrderAmount = 0 ")
    int schedulerUpdateStartMonth();

    @Query(value = "SELECT c from Customer c where c.officeAddress is not null or c.workingOffice is not null")
    List<Customer> findCuss();

}
