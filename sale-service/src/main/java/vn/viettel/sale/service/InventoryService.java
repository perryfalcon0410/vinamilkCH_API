package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.StockCountingFilter;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;

import java.util.Date;

public interface InventoryService {
    Response<Page<StockCountingDTO>> find( String stockCountingCode,
             Date fromDate,
             Date toDate, Pageable pageable);
    Response<Page<StockCountingDetailDTO>> getByStockCountingId(Long id, Pageable pageable);
    Response<Page<StockCountingDetailDTO>> importExcel(Long stockCountingId, String filePath, Pageable pageable);
}
