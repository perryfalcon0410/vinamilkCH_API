package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.service.dto.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


public interface InventoryService {
    Page<StockCountingDTO> index( String stockCountingCode,Long warehouseTypeId,
                                            LocalDateTime fromDate,
                                            LocalDateTime toDate,Long shopId, Pageable pageable);

    CoverResponse<List<StockCountingExcel>, TotalStockCounting> getByStockCountingId(Long id);

    CoverResponse<StockCountingImportDTO, InventoryImportInfo> importExcel(Long shopId, MultipartFile file, Pageable pageable, String searchKeywords) throws IOException;

    ResponseMessage updateStockCounting(Long stockCountingId, String userAccount, List<StockCountingUpdateDTO> details);

    //lay cac sp ton kho
    Object getAll(Long shopId, String searchKeywords);

    Long createStockCounting(List<StockCountingDetailDTO> stockCountingDetails, Long userId, Long shopId, Boolean override);

    Boolean checkInventoryInDay(Long shopId);
}
