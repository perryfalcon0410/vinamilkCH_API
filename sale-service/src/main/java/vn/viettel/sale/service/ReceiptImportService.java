package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.dto.*;

import java.util.Date;
import java.util.List;

public interface ReceiptImportService {
    /////////////////////////////////////////////////////// Crud
    Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>> find(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Long shopId, Pageable pageable);
    Response<Object> createReceipt(ReceiptCreateRequest request,Long userId,Long shopId);
    Response<Object> updateReceiptImport(ReceiptUpdateRequest request, Long id);
    Response<String> removeReceiptImport(Long id,Integer type);
    ////////////////////////////////////////////////////// get for update
    Response<Object> getForUpdate(Integer type,Long id);
    ///////////////////////////////////////////////////// get for create
    Response<List<PoConfirmDTO>> getListPoConfirm();
    Response<List<StockAdjustmentDTO>> getListStockAdjustment();
    Response<List<StockBorrowingDTO>> getListStockBorrowing(Long toShopId);
    ///////////////////////////////////////////////////// get detail PoConfirm
    Response<CoverResponse<List<PoDetailDTO>,TotalResponse>> getPoDetailByPoId(Long id,Long shopId);
    Response<CoverResponse<List<PoDetailDTO>,TotalResponse>> getPoDetailByPoIdAndPriceIsNull(Long id,Long shopId);
    /////////////////////////////////////////////////////get detail Stock Adjustment
    Response<List<StockAdjustmentDetailDTO>> getStockAdjustmentDetail(Long id);
    //////////////////////////////////////////////////// get detail Stock borrowing
    Response<List<StockBorrowingDetailDTO>> getStockBorrowingDetail(Long id);
    //////////////////////////////////////////////////// get detail poTrans
    Response<Object> getTransDetail(Integer type, Long id, Long shopId);

    Response<String> setNotImport(Long id);

}