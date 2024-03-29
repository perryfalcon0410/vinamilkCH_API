package vn.viettel.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> getCustomerByIdNo(String idNo);

    List<Customer> getAllByMobiPhoneAndStatus(String mobiPhone, Integer status);

    @Query(value = "SELECT c FROM Customer c " +
            " WHERE (:status is null Or c.status = :status)" +
            " AND (:nameCode is null Or c.nameText like %:nameCode% Or c.customerCode like %:nameCode%) " +
            " AND (:phone is null Or c.mobiPhone like %:phone)" +
            " AND (:idNo is null Or c.idNo like %:idNo%)" +
            " AND (:genderId is null Or c.genderId = :genderId)" +
            " AND (:customerTypeId is null Or c.customerTypeId = :customerTypeId)" +
            " AND (COALESCE(:areaIds, NULL) is null Or c.areaId in :areaIds)" +
            " AND (:isShop is null Or :isShop is false Or c.shopId = :shopId)" +
            " ORDER BY decode(c.shopId, :shopId, 0) " +
            "")
    Page<Customer> findAllBy(Integer status, String nameCode, String phone, String idNo, Long genderId, Long customerTypeId, List<Long> areaIds, Long shopId, Boolean isShop, Pageable pageable);

    @Query(value = "SELECT c FROM Customer c WHERE c.shopId =:shopId AND c.customerCode NOT LIKE '%.KA___' ORDER BY c.customerCode desc ")
    Page<Customer> getLastCustomerNumber(@Param("shopId") Long shopId,  Pageable pageable);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerDTO (c.id, c.firstName, c.lastName, c.nameText, c.customerCode, c.mobiPhone," +
            " c.customerTypeId, c.street, c.address, c.shopId, c.phone, c.workingOffice, c.officeAddress, c.taxCode, c.totalBill, c.dob) " +
            " FROM Customer c WHERE c.shopId =:shopId AND c.isDefault = true " +
            " AND c.status = 1 ORDER BY c.updatedAt DESC")
    List<CustomerDTO> getCustomerDefault(Long shopId);

    @Query(value = "SELECT DISTINCT c.id FROM Customer c where ( c.customerCode like %:nameOrCode% OR c.nameText like %:nameOrCode% ) " +
            "and c.mobiPhone like %:customerPhone and c.id in (:ids)")
    List<Long> getCustomerIds(String nameOrCode, String customerPhone, List<Long> ids);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerDTO (c.id, c.firstName, c.lastName, c.nameText, c.customerCode, c.mobiPhone," +
            " c.customerTypeId, c.street, c.address, c.shopId, c.phone, c.workingOffice, c.officeAddress, c.taxCode, c.totalBill, c.dob) " +
            " FROM Customer c WHERE c.status = 1 AND ( :shopId IS NULL OR c.shopId =:shopId )" +
            " AND ( c.nameText like %:nameCode% OR c.customerCode like %:nameCode% " +
            "   OR c.phone like %:phone OR c.mobiPhone like %:phone OR c.addressText like %:address% ) " +
            " ORDER BY decode(c.shopId, :shopId, 0), c.customerCode, c.nameText")
    Page<CustomerDTO> searchForSale(Long shopId, String nameCode, String address, String phone, Pageable pageable);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerDTO (c.id, c.firstName, c.lastName, c.nameText, c.customerCode, c.mobiPhone," +
            " c.customerTypeId, c.street, c.address, c.shopId, c.phone, c.workingOffice, c.officeAddress, c.taxCode, c.totalBill, c.dob) " +
            " FROM Customer c WHERE c.status = 1 AND ( :shopId IS NULL OR c.shopId =:shopId )" +
            " AND ( c.phone like %:phone OR c.mobiPhone like %:phone ) " +
            " ORDER BY decode(c.shopId, :shopId, 0), c.customerCode, c.nameText")
    Page<CustomerDTO> searchForSaleFone(Long shopId, String phone, Pageable pageable);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerDTO (c.id, c.firstName, c.lastName, c.nameText, c.customerCode, c.mobiPhone," +
            " c.customerTypeId, c.street, c.address, c.shopId, c.phone, c.workingOffice, c.officeAddress, c.taxCode, c.totalBill, c.dob) " +
            " FROM Customer c WHERE c.status = 1 " +
            " AND ( c.nameText like %:searchKeywords% OR c.customerCode like %:searchKeywords% ) " +
            "   AND (c.phone like %:mobiphone OR c.mobiPhone like %:mobiphone) " +
            "   AND (:workingOffice is null OR c.workingOfficeText like %:workingOffice%) " +
            "   AND (:officeAddress is null OR c.officeAddressText like %:officeAddress%)  " +
            "   AND (:taxCode is null OR upper(c.taxCode) like %:taxCode%)  " +
            " ORDER BY decode(c.shopId, :shopId, 0), c.customerCode, c.nameText")
    Page<CustomerDTO> searchForRedInvoice(String searchKeywords, String mobiphone, String workingOffice, String officeAddress, String taxCode, Long shopId, Pageable pageable);

    @Modifying()
    @Query(value = "Update Customer SET dayOrderNumber = 0 , dayOrderAmount = 0 , updatedAt = sysdate, updatedBy= 'schedule' ")
    int schedulerUpdateStartDay();

    @Modifying()
    @Query(value = "Update Customer SET dayOrderNumber = 0 , dayOrderAmount = 0, monthOrderNumber = 0 , monthOrderAmount = 0 , updatedAt = sysdate, updatedBy= 'schedule' ")
    int schedulerUpdateStartMonth();

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerDTO(c.id, c.firstName, c.lastName, c.nameText, c.customerCode, c.mobiPhone," +
            " c.customerTypeId, c.street, c.address, c.shopId, c.phone, c.workingOffice, c.officeAddress, c.taxCode, c.totalBill, c.dob) " +
            " FROM Customer c WHERE c.status = 1 " +
            " AND ( c.nameText like %:name% OR c.customerCode like %:code% " +
            "   OR c.phone like %:phone OR c.mobiPhone like %:phone ) " +
            " ORDER BY c.customerCode, c.nameText, c.mobiPhone"
    )
    List<CustomerDTO> searchForAutoComplete(String name, String code, String phone);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerDTO(c.id, c.firstName, c.lastName, c.nameText, c.customerCode, c.mobiPhone," +
            " c.customerTypeId, c.street, c.address, c.shopId, c.phone, c.workingOffice, c.officeAddress, c.taxCode, c.totalBill, c.dob) " +
            " FROM Customer c WHERE c.status = 1 " +
            " AND ( c.nameText like %:name% OR c.customerCode like %:code% " +
            "   OR c.phone like %:phone OR c.mobiPhone like %:phone ) " +
            " ORDER BY decode(c.shopId, :shopId, 0), c.customerCode, c.nameText, c.mobiPhone"
    )
    Page<CustomerDTO> searchCustomer(String name, String code, String phone, Long shopId, Pageable pageable);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerDTO(c.id, c.firstName, c.lastName, c.nameText, c.customerCode, c.mobiPhone," +
            " c.customerTypeId, c.street, c.address, c.shopId, c.phone, c.workingOffice, c.officeAddress, c.taxCode, c.totalBill, c.dob) " +
            " FROM Customer c WHERE c.status = 1 " +
            " AND ( c.nameText like %:name% OR c.customerCode like %:code% " +
            "   OR c.phone like %:phone OR c.mobiPhone like %:phone OR c.addressText like %:address% ) " +
            " ORDER BY c.customerCode, c.nameText, c.mobiPhone"
    )
    List<CustomerDTO> searchForAutoComplete(String name, String code, String address, String phone);

    @Query(value = "SELECT distinct c.id " +
            " FROM Customer c WHERE 1 = 1 " +
            " AND ( c.nameText like %:name% OR c.customerCode like %:code% " +
            "   OR c.phone like %:phone OR c.mobiPhone like %:phone ) "
    )
    List<Long> getCustomersIds(String name, String code, String phone);
}
