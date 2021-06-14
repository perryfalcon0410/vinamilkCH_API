package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptImportService {
    /////////////////////////////////////////////////////// Crud
    CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> find(String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate, Integer type, Long shopId, Pageable pageable);
    ResponseMessage createReceipt(ReceiptCreateRequest request,Long userId,Long shopId);
    ResponseMessage updateReceiptImport(ReceiptUpdateRequest request, Long id, String userName);
    ResponseMessage removeReceiptImport(Long id,Integer type,String userName);
    ////////////////////////////////////////////////////// get for update
    Object getForUpdate(Integer type,Long id);
    ///////////////////////////////////////////////////// get for create
    List<PoConfirmDTO> getListPoConfirm(Long shopId);
    List<StockAdjustmentDTO> getListStockAdjustment(Long shopId, Pageable pageable);
    List<StockBorrowingDTO> getListStockBorrowing(Long shopId,Pageable pageable);
    ///////////////////////////////////////////////////// get detail PoConfirm
    CoverResponse<List<PoDetailDTO>, TotalResponseV1> getPoDetailByPoId(Long id, Long shopId);
    CoverResponse<List<PoDetailDTO>,TotalResponseV1> getPoDetailByPoIdAndPriceIsNull(Long id,Long shopId);
    /////////////////////////////////////////////////////get detail Stock Adjustment
    CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse> getStockAdjustmentDetail(Long id);
    //////////////////////////////////////////////////// get detail Stock borrowing
    CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse> getStockBorrowingDetail(Long id);
    //////////////////////////////////////////////////// get detail poTrans
    Object getTransDetail(Integer type, Long id, Long shopId);

    ResponseMessage setNotImport(Long id, NotImportRequest request);
    WareHouseTypeDTO getWareHouseTypeName(Long shopId);

}