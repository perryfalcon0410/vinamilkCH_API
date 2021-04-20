package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface CustomerTypeRepository extends BaseRepository<CustomerType> {
    Optional<CustomerType> findById(Long id);

    @Query(value = "SELECT DISTINCT ct.* FROM CUSTOMER_TYPE ct JOIN CUSTOMERS c ON c.CUSTOMER_TYPE_ID = ct.Id " +
            " WHERE  c.SHOP_ID =:shopId AND c.IS_DEFAULT = 1 AND ct.STATUS = 1 AND c.STATUS = 1 ", nativeQuery = true)
    CustomerType getWareHouseTypeIdByShopId(@Param("shopId") Long shopId);

    @Query(value = "SELECT * FROM CUSTOMER_TYPE WHERE POS_MODIFY_CUS = 1 AND STATUS = 1 AND DELETED_AT IS NULL " +
            "ORDER BY CODE DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", nativeQuery = true)
    Optional<CustomerType> getCustomerTypeDefault();

}
