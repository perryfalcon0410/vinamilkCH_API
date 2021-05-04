package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.RptStockAggregated;

import java.util.Date;
import java.util.Optional;

public interface RptStockAggregatedRepository extends BaseRepository<RptStockAggregated>, JpaSpecificationExecutor<RptStockAggregated> {

    @Query(value = "SELECT * FROM RPT_STOCK_AGGREGATED " +
            "WHERE SHOP_ID =:shopId AND WAREHOUSE_TYPE_ID =:warehouseTypeId AND " +
            "PRODUCT_ID =:productId AND RPT_DATE <=:rptDate " +
            "ORDER BY RPT_DATE DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", nativeQuery = true)
    Optional<RptStockAggregated> getRptStockAggregated(Long shopId, Long warehouseTypeId, Long productId, Date rptDate);
}
