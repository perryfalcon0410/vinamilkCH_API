package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.WareHouseType;
import vn.viettel.core.repository.BaseRepository;

public interface WareHouseTypeRepository extends BaseRepository<WareHouseType> {
    @Query(value = "SELECT  * FROM WAREHOUSE_TYPE w w.ID =:cusTypeId " , nativeQuery = true)
    WareHouseType getWareHouseTypeIdByCustomer(Long cusTypeId);
}
