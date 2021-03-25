package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.stock.StockAdjustmentDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockAdjustmentDetailRepository extends BaseRepository<StockAdjustmentDetail>{
    List<StockAdjustmentDetail> findByAdjustmentId(Long adjustmentId);
}
