package vn.viettel.sale.repository;

import vn.viettel.sale.entities.StockAdjustmentDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockAdjustmentDetailRepository extends BaseRepository<StockAdjustmentDetail>{
    List<StockAdjustmentDetail> getStockAdjustmentDetailByAdjustmentId(Long adjustmentId);
}
