package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.stock.StockCounting;
import vn.viettel.core.db.entity.stock.StockCountingDetail;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.*;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import java.util.Date;


public interface InventoryService {
    Response<Page<StockCountingDTO>> find( String stockCountingCode,
             Date fromDate,
             Date toDate, Pageable pageable);

    Response<CoverResponse<Page<StockCountingDetailDTO>, TotalStockCounting>> getByStockCountingId(Long id, Pageable pageable);

    Response<StockCountingImportDTO> importExcel(List<StockCountingDetailDTO> stockCountingDetails, String filePath);

    Response<List<StockCountingDetail>> updateStockCounting(Long stockCountingId, Long userId, List<StockCountingDetailDTO> details);

    Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>> getAll(Pageable pageable);

    StockCounting createStockCounting(List<StockCountingDetailDTO> stockCountingDetails, Long userId, Long shopId, Boolean override);

}
