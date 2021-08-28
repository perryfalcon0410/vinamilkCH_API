package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.CustomerType;

import java.util.List;
import java.util.Optional;

public interface CustomerTypeRepository extends BaseRepository<CustomerType> {
    Optional<CustomerType> findById(Long id);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerTypeDTO (ct.id, ct.wareHouseTypeId) " +
            " FROM CustomerType ct JOIN Customer c ON c.customerTypeId = ct.id AND c.shopId =:shopId AND c.isDefault = true AND c.status = 1  " +
            " WHERE ct.status = 1 " +
            " ORDER BY ct.updatedAt DESC ")
    List<CustomerTypeDTO> getWareHouseTypeIdByShopId(Long shopId);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerTypeDTO (ct.id, ct.code, ct.name, ct.kaCustomerType, ct.posModifyCustomer, ct.wareHouseTypeId, ct.status)" +
            " FROM CustomerType ct JOIN Customer c ON c.customerTypeId = ct.id " +
            " WHERE  c.id =:customerId AND ct.status = 1 AND c.status = 1 ")
    List<CustomerTypeDTO> getByCustomerId(Long customerId);

    @Query(value = "SELECT new vn.viettel.core.dto.customer.CustomerTypeDTO (ct.id, ct.wareHouseTypeId) " +
            " FROM CustomerType ct WHERE ct.posModifyCustomer = 1 AND ct.status = 1 " +
            " ORDER BY ct.code ASC ")
    List<CustomerTypeDTO> getCustomerTypeDefault();

    @Query(value = "SELECT ct FROM CustomerType ct WHERE ct.status = 1 AND ct.id IN (:customerTypeIds)")
    List<CustomerType> findByIds(List<Long> customerTypeIds);

    @Query(value = "SELECT DISTINCT ct FROM CustomerType ct " +
            " WHERE  ct.wareHouseTypeId =:warehouseId AND ct.status = 1 " +
            " ORDER BY ct.id ")
    List<CustomerType> getByWarehouse(Long warehouseId);
}
