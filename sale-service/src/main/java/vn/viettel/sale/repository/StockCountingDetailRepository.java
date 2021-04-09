package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.stock.StockCountingDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockCountingDetailRepository extends BaseRepository<StockCountingDetail> {
    List<StockCountingDetail> findByStockCountingId(Long id);
}
