package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.service.dto.*;

import java.util.Date;

public interface ReceiptService {
    Response<Page<PoTransDTO>> getAll(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Pageable pageable);
    Response<String> createReceipt(ReceiptCreateRequest request, Long userId);
    Response<PoTransDTO> getOnePoTransById(Long tranId);
    Response<StockAdjustmentTransDTO> getOneStockAdjustmentTransById(Long tranId);
    Response<StockBorrowingTransDTO> getOneStockBorrowingTransById(Long tranId);
}