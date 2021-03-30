package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.db.entity.stock.StockAdjustmentTrans;
import vn.viettel.core.repository.BaseRepository;

public interface StockAdjustmentTransRepository extends BaseRepository<StockAdjustmentTrans> {
    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS", nativeQuery = true)
    int getQuantityStockAdjustmentTrans();
    StockAdjustmentTrans getStockAdjustmentTransByIdAndDeletedAtIsNull(Long transId);
    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS WHERE TO_CHAR(TRANS_DATE,'DD') = TO_CHAR(SYSDATE,'DD')  ", nativeQuery = true)
    int getQuantityAdjustmentTransVer2();
}