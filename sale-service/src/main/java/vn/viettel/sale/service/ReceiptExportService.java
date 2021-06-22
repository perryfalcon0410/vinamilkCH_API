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

    /** Lấy danh sách phiếu xuất hàng **/
    CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> find(String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate, Integer type, Long shopId, Pageable pageable);

    /** Tạo mới phiếu xuất hàng **/
    ResponseMessage createReceipt(ReceiptExportCreateRequest request, Long userId, Long shopId);

    /** Cập nhật phiếu xuất hàng **/
    ResponseMessage updateReceiptExport(ReceiptExportUpdateRequest request, Long id);

    /** Xóa phiếu xuất hàng **/
    ResponseMessage removeReceiptExport(Integer type,Long id);

    /** Lấy danh sách phiếu nhập PO **/
    Page<PoTransDTO> getListPoTrans( String transCode, String redInvoiceNo, String internalNumber, String poNo, LocalDateTime fromDate, LocalDateTime toDate,Long shopId, Pageable pageable);

    /** Lấy danh sách phiếu xuất điều chỉnh **/
    List<StockAdjustmentDTO> getListStockAdjustment(Long shopId, Pageable pageable);

    /** Lấy danh sách phiếu xuất vay mượn **/
    List<StockBorrowingDTO> getListStockBorrowing(Long shopId,Pageable pageable);

}
