package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.service.dto.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;


public interface InventoryService {
    Response<Page<StockCountingDTO>> index( String stockCountingCode,Long warehouseTypeId,
             Date fromDate,
             Date toDate, Pageable pageable);

    Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>> getByStockCountingId(Long id, Pageable pageable);

    CoverResponse<StockCountingImportDTO, Integer> importExcel(MultipartFile file, Pageable pageable) throws IOException;

    Response<List<StockCountingDetail>> updateStockCounting(Long stockCountingId, Long userId, List<StockCountingDetailDTO> details);

    Object getAll(Pageable pageable, Boolean isPaging);

    Object createStockCounting(List<StockCountingDetailDTO> stockCountingDetails, Long userId, Long shopId, Boolean override);

    Response<Boolean> checkInventoryInDay(Long shopId);
}
