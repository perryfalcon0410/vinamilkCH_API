package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.service.dto.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


public interface InventoryService {
    Response<Page<StockCountingDTO>> index( String stockCountingCode,Long warehouseTypeId,
                                            LocalDateTime fromDate,
                                            LocalDateTime toDate, Pageable pageable);

    Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>> getByStockCountingId(Long id, Pageable pageable);

    CoverResponse<StockCountingImportDTO, InventoryImportInfo> importExcel(MultipartFile file, Pageable pageable, String searchKeywords) throws IOException;

    List<StockCountingDetail> updateStockCounting(Long stockCountingId, String userAccount, List<StockCountingUpdateDTO> details);

    Object getAll(Pageable pageable, Boolean isPaging, String searchKeywords);

    Object createStockCounting(List<StockCountingDetailDTO> stockCountingDetails, Long userId, Long shopId, Boolean override);

    Boolean checkInventoryInDay(Long shopId);
}
