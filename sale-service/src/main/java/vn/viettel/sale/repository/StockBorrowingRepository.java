package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockBorrowing;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockBorrowingRepository extends BaseRepository<StockBorrowing> {
    @Query(value = "SELECT * FROM STOCK_BORROWING  WHERE STATUS = 1 ", nativeQuery = true)
    List<StockBorrowing> getStockBorrowing();

    @Query(value = "SELECT * FROM STOCK_BORROWING  WHERE STATUS = 4  ", nativeQuery = true)
    List<StockBorrowing> getStockBorrowingExport();
}
