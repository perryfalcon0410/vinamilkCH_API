package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockAdjustment;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
public interface StockAdjustmentRepository extends BaseRepository<StockAdjustment> {
    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT  WHERE STATUS = 1 AND TYPE = 1 ", nativeQuery = true)
    List<StockAdjustment> getStockAdjustment();

    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT  WHERE STATUS = 3 AND TYPE = 2 ", nativeQuery = true)
    List<StockAdjustment> getStockAdjustmentExport();
}
