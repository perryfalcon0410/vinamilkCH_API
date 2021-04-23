package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockBorrowing;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockBorrowingRepository extends BaseRepository<StockBorrowing> {
    @Query(value = "SELECT * FROM STOCK_BORROWING  WHERE TYPE = 1 AND STATUS IN (1,3) ", nativeQuery = true)
    List<StockBorrowing> getStockBorrowing();

    @Query(value = "SELECT * FROM STOCK_BORROWING  WHERE TYPE = 2 STATUS = 1  ", nativeQuery = true)
    List<StockBorrowing> getStockBorrowingExport();
}
