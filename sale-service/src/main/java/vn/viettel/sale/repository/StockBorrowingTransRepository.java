package vn.viettel.sale.repository;

import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.StockBorrowingTrans;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface StockBorrowingTransRepository extends BaseRepository<StockBorrowingTrans> {
    @Query(value = "SELECT COUNT(ID) FROM STOCK_BORROWING_TRANS", nativeQuery = true)
    int getQuantityStockBorrowingTrans();
    Optional<StockBorrowingTrans> getStockBorrowingTransByIdAndDeletedAtIsNull(Long tranId);
}
