package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.stock.PoTransDetail;
import vn.viettel.core.db.entity.stock.StockAdjustmentTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockAdjustmentTransDetailRepository extends BaseRepository<StockAdjustmentTransDetail> {
    List<StockAdjustmentTransDetail> getStockAdjustmentTransDetailsByTransIdAndDeletedAtIsNull(Long id);
}
