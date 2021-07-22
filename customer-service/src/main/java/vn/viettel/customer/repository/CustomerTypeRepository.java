package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.customer.entities.CustomerType;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerTypeRepository extends BaseRepository<CustomerType> {
    Optional<CustomerType> findById(Long id);

    @Query(value = "SELECT DISTINCT ct.* FROM CUSTOMER_TYPE ct JOIN CUSTOMERS c ON c.CUSTOMER_TYPE_ID = ct.Id " +
            " WHERE  c.SHOP_ID =:shopId AND c.IS_DEFAULT = 1 AND ct.STATUS = 1 AND c.STATUS = 1 " +
            " ORDER BY ct.UPDATED_AT DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ", nativeQuery = true)
    CustomerType getWareHouseTypeIdByShopId(@Param("shopId") Long shopId);

    @Query(value = "SELECT * FROM CUSTOMER_TYPE WHERE POS_MODIFY_CUS = 1 AND STATUS = 1 " +
            "ORDER BY CODE ASC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", nativeQuery = true)
    Optional<CustomerType> getCustomerTypeDefault();

    Optional<CustomerType> findCustomerTypeById(Long customerTypeId);

    @Query(value = "SELECT ct.WAREHOUSE_TYPE_ID FROM CUSTOMER_TYPE ct " +
            "JOIN CUSTOMERS c ON c.CUSTOMER_TYPE_ID = ct.id " +
            "WHERE c.IS_DEFAULT = 1 AND ct.STATUS = 1 AND c.STATUS = 1 " +
            "AND c.SHOP_ID = :id", nativeQuery = true)
    Long findWarehouseTypeIdByCustomer(Long id);

    @Query(value = "SELECT ct FROM CustomerType ct WHERE ct.status = 1 AND ct.id IN (:customerTypeIds)")
    List<CustomerType> findByIds(List<Long> customerTypeIds);
}
