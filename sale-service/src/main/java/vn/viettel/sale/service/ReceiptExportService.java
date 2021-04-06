package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.stock.PoTransDetail;
import vn.viettel.core.messaging.Response;

import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.service.dto.*;

import java.util.Date;
import java.util.List;

public interface ReceiptExportService {

    /////////////////////////////////////////////////////////////////////////////////// Crud
    Response<Page<ReceiptImportListDTO>> index(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Pageable pageable);
    Response<String> createReceipt(ReceiptExportCreateRequest request, Long userId);
    Response<String> updatePoTransExport(ReceiptExportUpdateRequest request, Long id);
    Response<String> updateStockAdjustmentTransExport(ReceiptExportUpdateRequest request, Long id);
    Response<String> updateStockBorrowingTransExport(ReceiptExportUpdateRequest request, Long id);
    Response<String> removePoTransExport(Long id);
    Response<String> removeStockAdjustmentTransExport(Long id);
    Response<String> removeStockBorrowingTransExport(Long id);
    ////////////////////////////////////////////////////////////////////////////////// get for update
    /*Response<PoTransDTO> getPoTransExportById(Long transId);
    Response<StockAdjustmentTransDTO> getStockAdjustmentTransById(Long transId);
    Response<StockBorrowingTransDTO> getStockBorrowingTransById(Long transId);*/
    //////////////////////////////////////////////////////////////////////////////////get for create
    Response<Page<PoTransDTO>> getListPoTrans(String transCode,String redInvoiceNo,String internalNumber,String poNo,Date fromDate,Date toDate, Pageable pageable);
    /*Response<List<StockAdjustmentDTO>> getListStockAdjustmentExport();
    Response<List<StockBorrowingDTO>> getListStockBorrowingExport();*/
    ////////////////////////////////////////////////////////////////////////////////get detail
   /* Response<List<PoTransDetailDTO>> getPoTransDetailExportByTransId(Long transId);*/

}
