package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockAdjustmentTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockAdjustmentTransDetailRepository extends BaseRepository<StockAdjustmentTransDetail> {

    List<StockAdjustmentTransDetail> getStockAdjustmentTransDetailsByTransId(Long id);

    @Query(value = "SELECT sat FROM StockAdjustmentTransDetail sat WHERE sat.transId =:transId ")
    List<StockAdjustmentTransDetail> getStockAdjustmentTransDetail(Long transId);
}
