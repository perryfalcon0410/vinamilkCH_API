package vn.viettel.customer.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerTypeRepository extends BaseRepository<CustomerType> {
    Optional<CustomerType> findById(Long id);

    @Query(value = "SELECT DISTINCT ct.* FROM CUSTOMER_TYPE ct JOIN CUSTOMERS c ON c.CUSTOMER_TYPE_ID = ct.Id " +
            " WHERE  c.SHOP_ID =:shopId AND c.IS_DEFAULT =:shopId ", nativeQuery = true)
    CustomerType getWareHouseTypeIdByShopId(@Param("shopId") Long shopId);
}
