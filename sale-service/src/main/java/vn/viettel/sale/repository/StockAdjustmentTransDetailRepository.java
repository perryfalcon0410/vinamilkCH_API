package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockAdjustmentTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockAdjustmentTransDetailRepository extends BaseRepository<StockAdjustmentTransDetail> {
    List<StockAdjustmentTransDetail> getStockAdjustmentTransDetailsByTransId(Long id);

    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT_TRANS_DETAIL WHERE TRANS_ID =:transId AND DELETED_AT IS NULL ", nativeQuery = true)
    List<StockAdjustmentTransDetail> getStockAdjustmentTransDetail(Long transId);
}
