package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.StockBorrowingTrans;
import vn.viettel.core.repository.BaseRepository;

public interface StockBorrowingTransRepository extends BaseRepository<StockBorrowingTrans> {
    @Query(value = "SELECT COUNT(ID) FROM STOCK_BORROWING_TRANS", nativeQuery = true)
    int getQuantityStockBorrowingTrans();
    @Query(value = "SELECT COUNT(ID) FROM STOCK_BORROWING_TRANS WHERE STATUS = 2 ", nativeQuery = true)
    int getQuantityStockBorrowingTransExport();

    @Query(value = "SELECT * FROM STOCK_BORROWING_TRANS WHERE TYPE =1 AND DELETED_AT IS NULL AND TRANS_iD =:id " , nativeQuery = true)
    StockBorrowingTrans getStockBorrowingTransByIdAndDeletedAtIsNull(Long transId);

    @Query(value = "SELECT * FROM STOCK_BORROWING_TRANS WHERE TYPE =2 AND DELETED_AT IS NULL AND TRANS_iD =:id " , nativeQuery = true)
    StockBorrowingTrans getStockBorrowingTransExportById(Long transId);
}
