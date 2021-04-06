package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.common.WareHouseType;
import vn.viettel.core.repository.BaseRepository;

public interface WareHouseTypeRepository extends BaseRepository<WareHouseType> {
    /*@Query(value = "SELECT  * FROM WAREHOUSE_TYPE w JOIN CUSTOMER_TYPE c ON c.WAREHOUSE_TYPE_ID = w.ID " +
            " JOIN CUSTOMER cus ON cus.CUSTOMER_TYPE_ID = c.ID JOIN SHOP s ON s.ID =cus.SHOP_ID AND  ", nativeQuery = true)
    WareHouseType findWareHouseTypeByShopId(Long shopId);*/
}
