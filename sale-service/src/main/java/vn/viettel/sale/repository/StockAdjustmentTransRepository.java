package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockAdjustmentTrans;
import vn.viettel.core.repository.BaseRepository;

import java.time.LocalDateTime;

public interface StockAdjustmentTransRepository extends BaseRepository<StockAdjustmentTrans>, JpaSpecificationExecutor<StockAdjustmentTrans> {

    @Query(value = "SELECT COUNT(sat.id) FROM StockAdjustmentTrans sat WHERE sat.type = 1 and sat.transDate >= :date ")
    int countImport(LocalDateTime date);

    @Query(value = "SELECT COUNT(sat.id) FROM StockAdjustmentTrans sat WHERE sat.type = 2 and sat.transDate >= :date ")
    int countExport(LocalDateTime date);

}