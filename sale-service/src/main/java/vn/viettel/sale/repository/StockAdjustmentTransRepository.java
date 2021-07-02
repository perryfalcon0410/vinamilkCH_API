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

    @Query(value = "SELECT COUNT(sat.id) FROM StockAdjustmentTrans sat")
    int getQuantityStockAdjustmentTrans();

    StockAdjustmentTrans getStockAdjustmentTransById(Long transId);

    @Query(value = "SELECT COUNT(ID) FROM STOCK_ADJUSTMENT_TRANS WHERE TO_CHAR(TRANS_DATE,'DD') = TO_CHAR(SYSDATE,'DD')  ", nativeQuery = true)
    int getQuantityAdjustmentTransVer2();

    @Query(value = "SELECT sat FROM StockAdjustmentTrans sat WHERE sat.id =:id AND sat.type = 1 ")
    StockAdjustmentTrans getAdjustTransImportById(Long id);

    @Query(value = "SELECT COUNT(sat.id) FROM StockAdjustmentTrans sat WHERE sat.type = 2 AND sat.status = 1 ")
    int getQuantityStockAdjustTransExport();

}