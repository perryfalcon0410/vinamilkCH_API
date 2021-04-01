package vn.viettel.sale.service;

import vn.viettel.core.db.entity.stock.PoTransDetail;
import vn.viettel.core.messaging.Response;

import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.service.dto.*;

import java.util.List;

public interface ReceiptExportService {
    Response<String> createReceipt(ReceiptExportCreateRequest request, Long userId);
    Response<PoTransDTO> getPoTransExportById(Long transId);
    Response<StockAdjustmentTransDTO> getStockAdjustmentTransById(Long transId);
    Response<StockBorrowingTransDTO> getStockBorrowingTransById(Long transId);
    Response<List<StockAdjustmentDTO>> getListStockAdjustmentExport();
    Response<List<StockBorrowingDTO>> getListStockBorrowingExport();
    Response<String> updatePoTransExport(ReceiptExportUpdateRequest request, Long id);
    Response<String> updateStockAdjustmentTransExport(ReceiptExportUpdateRequest request, Long id);
    Response<String> updateStockBorrowingTransExport(ReceiptExportUpdateRequest request, Long id);
    Response<String> removePoTransExport(Long id);
    Response<String> removeStockAdjustmentTransExport(Long id);
    Response<String> removeStockBorrowingTransExport(Long id);
    Response<List<PoTransDetailDTO>> getPoTransDetailExportByTransId(Long transId);

}
