package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockBorrowingTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockBorrowingTransDetailRepository extends BaseRepository<StockBorrowingTransDetail> {
    List<StockBorrowingTransDetail> getStockBorrowingTransDetailByTransIdAndDeletedAtIsNull(Long id);
    List<StockBorrowingTransDetail> getStockBorrowingTransDetailByTransId(Long id);

    @Query(value = "SELECT * FROM STOCK_BORROWING_TRANS_DETAIL WHERE TRANS_ID =:transId AND DELETED_AT IS NULL ", nativeQuery = true)
    List<StockBorrowingTransDetail> getStockBorrowingTransDetail(Long transId);
}
