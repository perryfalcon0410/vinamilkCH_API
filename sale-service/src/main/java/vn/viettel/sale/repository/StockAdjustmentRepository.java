package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockAdjustment;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.service.dto.StockAdjustmentDTO;

import java.util.List;
public interface StockAdjustmentRepository extends BaseRepository<StockAdjustment> {

    @Query(value = "SELECT st FROM StockAdjustment st  WHERE st.shopId=:shopId AND st.status = 1 AND st.type = 1 " +
            " ORDER BY st.adjustmentDate desc, st.adjustmentCode desc ")
    List<StockAdjustment> getStockAdjustment(Long shopId);

    @Query("SELECT NEW vn.viettel.sale.service.dto.StockAdjustmentDTO(sa.id, sa.adjustmentCode, sa.adjustmentDate, sa.shopId, sa.type," +
            " sa.status, sa.wareHouseTypeId, sa.reasonId, sa.description,w.wareHouseTypeName)" +
            " FROM StockAdjustment sa" +
            " JOIN WareHouseType w on w.id = sa.wareHouseTypeId " +
            " WHERE sa.shopId =:shopId AND sa.type = 2 AND sa.status = 1 " +
            " ORDER BY sa.adjustmentDate desc, sa.adjustmentCode desc ")
    List<StockAdjustmentDTO> getStockAdjustmentExport(Long shopId);
}
