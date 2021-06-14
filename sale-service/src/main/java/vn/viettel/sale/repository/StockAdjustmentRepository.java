package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockAdjustment;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
public interface StockAdjustmentRepository extends BaseRepository<StockAdjustment> {
    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT  WHERE SHOP_ID=:shopId AND STATUS = 1 AND TYPE = 1 ", nativeQuery = true)
    List<StockAdjustment> getStockAdjustment(Long shopId);

    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT WHERE SHOP_ID=:shopId AND STATUS = 1 AND TYPE = 2 ", nativeQuery = true)
    List<StockAdjustment> getStockAdjustmentExport(Long shopId);
}
