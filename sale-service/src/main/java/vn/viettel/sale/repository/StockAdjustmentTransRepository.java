package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockAdjustmentTrans;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface StockAdjustmentTransRepository extends BaseRepository<StockAdjustmentTrans>, JpaSpecificationExecutor<StockAdjustmentTrans> {
    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS", nativeQuery = true)
    int getQuantityStockAdjustmentTrans();

    StockAdjustmentTrans getStockAdjustmentTransById(Long transId);

    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS WHERE TO_CHAR(TRANS_DATE,'DD') = TO_CHAR(SYSDATE,'DD')  ", nativeQuery = true)
    int getQuantityAdjustmentTransVer2();
    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT_TRANS WHERE ID =:id AND TYPE = 1 ", nativeQuery = true)
    StockAdjustmentTrans getAdjustTransImportById(Long id);

    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS WHERE  TYPE = 2 AND STATUS = 1 ", nativeQuery = true)
    int getQuantityStockAdjustTransExport();

    Optional<StockAdjustmentTrans> getByTransCodeAndStatus(String transCode, Integer status);

}