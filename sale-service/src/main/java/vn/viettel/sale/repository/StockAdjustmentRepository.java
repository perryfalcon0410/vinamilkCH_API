package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockAdjustment;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.service.dto.StockAdjustmentDTO;
import vn.viettel.sale.service.dto.StockBorrowingDTO;

import java.util.List;
public interface StockAdjustmentRepository extends BaseRepository<StockAdjustment> {
    @Query(value = "SELECT * FROM STOCK_ADJUSTMENT  WHERE SHOP_ID=:shopId AND STATUS = 1 AND TYPE = 1 ", nativeQuery = true)
    List<StockAdjustment> getStockAdjustment(Long shopId);

    @Query("SELECT NEW vn.viettel.sale.service.dto.StockAdjustmentDTO(sa.id, sa.adjustmentCode, sa.adjustmentDate, sa.shopId, sa.type," +
            " sa.status, sa.wareHouseTypeId, sa.reasonId, sa.description)" +
            " FROM StockAdjustment sa WHERE sa.shopId =:shopId  AND sa.status = 1 ")
    List<StockAdjustmentDTO> getStockAdjustmentExport(Long shopId);
}
