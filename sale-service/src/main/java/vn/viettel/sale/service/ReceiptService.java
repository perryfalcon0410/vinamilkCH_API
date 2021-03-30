package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.service.dto.*;

import java.util.Date;

public interface ReceiptService {
    Response<Page<PoTransDTO>> getAll(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Pageable pageable);
    Response<String> createReceipt(ReceiptCreateRequest request,Long shopId,Long userId);
    Response<String> updatePoTrans(ReceiptUpdateRequest request, Long id);
    Response<String> updateStockAdjustmentTrans(ReceiptUpdateRequest request, Long id);
    Response<String> updateStockBorrowingTrans(ReceiptUpdateRequest request, Long id);
    void removePoTrans(Long id);
    void removeStockAdjustmentTrans(Long id);
    void removeStockBorrowingTrans(Long id);
    Response<PoTransDTO> getPoTransById(Long transId);
    Response<StockAdjustmentTransDTO> getStockAdjustmentById(Long transId);
    Response<StockBorrowingTransDTO> getStockBorrowingById(Long transId);
    Response<Page<PoTransDTO>> test( Pageable pageable);
}