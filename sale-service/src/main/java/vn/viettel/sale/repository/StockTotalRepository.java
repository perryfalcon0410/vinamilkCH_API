package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.stock.StockTotal;
import vn.viettel.core.repository.BaseRepository;

public interface StockTotalRepository extends BaseRepository<StockTotal> {
    StockTotal findByProductIdAndWareHouseTypeId(Long productId,Long wareHouseTypeId);
}
