package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockBorrowingTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockBorrowingTransDetailRepository extends BaseRepository<StockBorrowingTransDetail> {
    List<StockBorrowingTransDetail> getStockBorrowingTransDetailByTransId(Long id);

    @Query(value = "SELECT * FROM STOCK_BORROWING_TRANS_DETAIL WHERE TRANS_ID =:transId ", nativeQuery = true)
    List<StockBorrowingTransDetail> getStockBorrowingTransDetail(Long transId);
}
