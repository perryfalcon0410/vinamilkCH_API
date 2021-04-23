package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockCountingRepository extends BaseRepository<StockCounting>, JpaSpecificationExecutor<StockCounting> {
    @Query(value = "SELECT * FROM STOCK_COUNTING WHERE WAREHOUSE_TYPE_ID = :id AND " +
            "TO_CHAR(COUNTING_DATE,'DDMMYY') = TO_CHAR(SYSDATE,'DDMMYY')", nativeQuery = true)
    List<StockCounting> findByWareHouseTypeId(Long id);
}
