package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.stock.StockCountingDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.StockCountingFilter;
import vn.viettel.sale.service.dto.ExchangeTransImportDTO;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;

import java.util.List;

public interface InventoryService {
    Response<Page<StockCountingDTO>> find(StockCountingFilter filter, Pageable pageable);
    Response<Page<StockCountingDetailDTO>> getByStockCountingId(Long id, Pageable pageable);
    Response<ExchangeTransImportDTO> importExcel(List<StockCountingDetailDTO> stockCountingDetails, String filePath);
    Response<List<StockCountingDetail>> updateStockCounting(Long stockCountingId, Long userId, List<StockCountingDetailDTO> details);
}
