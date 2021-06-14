package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.dto.PoTransDTO;
import vn.viettel.sale.service.dto.ReceiptImportListDTO;
import vn.viettel.sale.service.dto.StockAdjustmentDTO;
import vn.viettel.sale.service.dto.StockBorrowingDTO;

import java.time.LocalDateTime;
import java.util.List;


public interface ReceiptExportService {

    /////////////////////////////////////////////////////////////////////////////////// Crud
    CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> find(String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate, Integer type, Long shopId, Pageable pageable);
    ResponseMessage createReceipt(ReceiptExportCreateRequest request, Long userId, Long shopId);
    ResponseMessage updateReceiptExport(ReceiptExportUpdateRequest request, Long id);
    ResponseMessage removeReceiptExport(Integer type,Long id);
    ////////////////////////////////////////////////////////////////////////////////// get for update
    Page<PoTransDTO> getListPoTrans( String transCode, String redInvoiceNo, String internalNumber, String poNo, LocalDateTime fromDate, LocalDateTime toDate,Long shopId, Pageable pageable);
    List<StockAdjustmentDTO> getListStockAdjustment(Long shopId, Pageable pageable);
    List<StockBorrowingDTO> getListStockBorrowing(Long shopId,Pageable pageable);

}
