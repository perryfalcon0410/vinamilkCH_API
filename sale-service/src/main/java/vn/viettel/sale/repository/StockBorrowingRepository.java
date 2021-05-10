package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockBorrowing;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockBorrowingRepository extends BaseRepository<StockBorrowing> {
    @Query(value = "SELECT * FROM STOCK_BORROWING WHERE TO_SHOP_ID =:toShopId AND STATUS_IMPORT = 1 ", nativeQuery = true)
    List<StockBorrowing> getStockBorrowing(Long toShopId);

    @Query(value = "SELECT * FROM STOCK_BORROWING WHERE SHOP_ID =:shopId AND STATUS_EXPORT = 1  ", nativeQuery = true)
    List<StockBorrowing> getStockBorrowingExport(Long shopId);
}
