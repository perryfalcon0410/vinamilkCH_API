package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.db.entity.stock.StockAdjustmentTrans;
import vn.viettel.core.repository.BaseRepository;

public interface StockAdjustmentTransRepository extends BaseRepository<StockAdjustmentTrans> {
    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS", nativeQuery = true)
    int getQuantityStockAdjustmentTrans();

    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT_TRANS WHERE TYPE =1 AND DELETED_AT IS NULL AND TRANS_iD =:transId  ", nativeQuery = true)
    StockAdjustmentTrans getStockAdjustmentTransByIdAndDeletedAtIsNull(Long transId);

    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS WHERE TO_CHAR(TRANS_DATE,'DD') = TO_CHAR(SYSDATE,'DD')  ", nativeQuery = true)
    int getQuantityAdjustmentTransVer2();
    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT_TRANS WHERE ID =:id AND TYPE = 1 ", nativeQuery = true)
    StockAdjustmentTrans getAdjustTransImportById(Long id);
    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS WHERE  TYPE = 2 ", nativeQuery = true)
    int getQuantityStockAdjustTransExport();

    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT_TRANS WHERE TYPE =2 AND DELETED_AT IS NULL AND TRANS_iD =:transId  ", nativeQuery = true)
    StockAdjustmentTrans getStockAdjustmentTransExportById(Long transId);

}