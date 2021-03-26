package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.StockAdjustmentTrans;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface StockAdjustmentTransRepository extends BaseRepository<StockAdjustmentTrans> {
    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS", nativeQuery = true)
    int getQuantityStockAdjustmentTrans();
    Optional<StockAdjustmentTrans> getStockAdjustmentTransByIdAndDeletedAtIsNull(Long tranId);
}