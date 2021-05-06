package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.*;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;


public interface InventoryService {
    Response<Page<StockCountingDTO>> index( String stockCountingCode,Long warehouseTypeId,
             Date fromDate,
             Date toDate, Pageable pageable);

    Response<CoverResponse<Page<StockCountingDetailDTO>, TotalStockCounting>> getByStockCountingId(Long id, Pageable pageable);

    Response<StockCountingImportDTO> importExcel(List<StockCountingDetailDTO> stockCountingDetails, String filePath) throws FileNotFoundException;

    Response<List<StockCountingDetail>> updateStockCounting(Long stockCountingId, Long userId, List<StockCountingDetailDTO> details);

    Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>> getAll(Pageable pageable);

    StockCounting createStockCounting(List<StockCountingDetailDTO> stockCountingDetails, Long userId, Long shopId, Boolean override);

}
