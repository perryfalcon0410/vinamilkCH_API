package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockAdjustmentTrans;
import vn.viettel.core.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StockAdjustmentTransRepository extends BaseRepository<StockAdjustmentTrans>, JpaSpecificationExecutor<StockAdjustmentTrans> {

    @Query(value = "SELECT p FROM StockAdjustmentTrans p WHERE p.createdAt>= :startDate And p.type =:type " +
            " ORDER BY p.transCode desc ")
    List<StockAdjustmentTrans> getLastAdjustTrans(Integer type, LocalDateTime startDate);
}