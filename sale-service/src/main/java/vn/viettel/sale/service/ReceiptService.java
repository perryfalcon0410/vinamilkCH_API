package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.stock.PoConfirm;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.service.dto.*;

import java.util.Date;
import java.util.List;

public interface ReceiptService {
    Response<Page<PoTransDTO>> getAll(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Pageable pageable);
    Response<String> createReceipt(ReceiptCreateRequest request,Long userId);
    Response<String> updatePoTrans(ReceiptUpdateRequest request, Long id);
    Response<String> updateStockAdjustmentTrans(ReceiptUpdateRequest request, Long id);
    Response<String> updateStockBorrowingTrans(ReceiptUpdateRequest request, Long id);
    Response<String> removePoTrans(Long id);
    Response<String> removeStockAdjustmentTrans(Long id);
    Response<String> removeStockBorrowingTrans(Long id);
    Response<PoTransDTO> getPoTransById(Long transId);
    Response<StockAdjustmentTransDTO> getStockAdjustmentById(Long transId);
    Response<StockBorrowingTransDTO> getStockBorrowingById(Long transId);
    Response<Page<ReceiptImportListDTO>> test(Integer type, Pageable pageable);
    Response<List<PoConfirmDTO>> getListPoConfirm();
    Response<List<StockAdjustmentDTO>> getListStockAdjustment();
    Response<List<StockBorrowingDTO>> getListStockBorrowing();
    Response<List<PoDetailDTO>> getPoDetailByPoId(Long id);
    Response<List<PoDetailDTO>> getPoDetailByPoIdAndPriceIsNull(Long id);
    Response<String> setNotImport(Long id);

}