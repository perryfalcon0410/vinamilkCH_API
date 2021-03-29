package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.StockBorrowingTrans;
import vn.viettel.core.repository.BaseRepository;

public interface StockBorrowingTransRepository extends BaseRepository<StockBorrowingTrans> {
    @Query(value = "SELECT COUNT(ID) FROM STOCK_BORROWING_TRANS", nativeQuery = true)
    int getQuantityStockBorrowingTrans();
    StockBorrowingTrans getStockBorrowingTransByIdAndDeletedAtIsNull(Long transId);
}
