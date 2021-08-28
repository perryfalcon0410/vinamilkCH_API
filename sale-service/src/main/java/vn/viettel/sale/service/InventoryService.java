package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.service.dto.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


public interface InventoryService {
    Page<StockCountingDTO> index( String stockCountingCode,Long warehouseTypeId,
                                            LocalDateTime fromDate,
                                            LocalDateTime toDate,Long shopId, Pageable pageable);

    CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> getByStockCountingId(Long id);

    CoverResponse<StockCountingImportDTO, InventoryImportInfo> importExcel(Long shopId, MultipartFile file, Pageable pageable, String searchKeywords,Long wareHouseTypeId) throws IOException;

    ResponseMessage updateStockCounting(Long stockCountingId, String userAccount, List<StockCountingUpdateDTO> details);

    //lay cac sp ton kho
    Object getAll(Long shopId, String searchKeywords,Long wareHouseTypeId);

    Long createStockCounting(List<StockCountingDetailDTO> stockCountingDetails, Long userId, Long shopId,Long wareHouseTypeId, Boolean override);

    Boolean checkInventoryInDay(Long wareHouseTypeId, Long shopId);

    StockCounting getStockCountingById(Long id);

    ByteArrayInputStream exportExcel(Long id, Long shopId) throws IOException;
}
