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

    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS WHERE  TYPE = 2 ", nativeQuery = true)
    int getQuantityStockAdjustTransExport();

    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT_TRANS WHERE TYPE =2 AND TRANS_ID =:transId  ", nativeQuery = true)
    StockAdjustmentTrans getStockAdjustmentTransExportById(Long transId);

    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT_TRANS WHERE TYPE = 1 ", nativeQuery = true)
    Page<StockAdjustmentTrans> getStockAdjustmentTransImport(Specification<StockAdjustmentTrans> and, Pageable pageable);

    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT_TRANS WHERE TYPE = 2 ", nativeQuery = true)
    Page<StockAdjustmentTrans> getStockAdjustmentTransExport(Specification<StockAdjustmentTrans> and, Pageable pageable);

    Optional<StockAdjustmentTrans> getByTransCodeAndStatus(String transCode, Integer status);

}