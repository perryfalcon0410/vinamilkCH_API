package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.StockCountingFilter;
import vn.viettel.sale.service.dto.ReceiptImportListDTO;
import vn.viettel.sale.service.dto.StockCountingDTO;

import java.util.Date;

public interface InventoryService {
    Response<Page<StockCountingDTO>> find(StockCountingFilter filter, Pageable pageable);
}
