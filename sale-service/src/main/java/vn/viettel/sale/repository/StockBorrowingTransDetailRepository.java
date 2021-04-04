package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.stock.StockAdjustmentTransDetail;
import vn.viettel.core.db.entity.stock.StockBorrowingTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockBorrowingTransDetailRepository extends BaseRepository<StockBorrowingTransDetail> {
    List<StockBorrowingTransDetail> getStockBorrowingTransDetailByTransIdAndDeletedAtIsNull(Long id);
    List<StockBorrowingTransDetail> getStockBorrowingTransDetailByTransId(Long id);

}
