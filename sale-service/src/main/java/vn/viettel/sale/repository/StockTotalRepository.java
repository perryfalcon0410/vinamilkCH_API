package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.StockTotal;
import vn.viettel.core.repository.BaseRepository;

public interface StockTotalRepository extends BaseRepository<StockTotal> {
    StockTotal findStockTotalByProductIdAndWareHouseId(Long productId, Long warehouseId);
    StockTotal findByWareHouseId(Long id);
}