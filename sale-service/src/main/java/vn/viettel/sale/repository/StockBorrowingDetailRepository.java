package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.stock.StockAdjustmentDetail;
import vn.viettel.core.db.entity.stock.StockBorrowingDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockBorrowingDetailRepository extends BaseRepository<StockBorrowingDetail> {
    List<StockBorrowingDetail> findByBorrowingId(Long borrowingId);
}
