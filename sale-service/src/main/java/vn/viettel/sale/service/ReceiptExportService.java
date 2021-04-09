package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.stock.PoTransDetail;
import vn.viettel.core.messaging.Response;

import vn.viettel.sale.messaging.PoTransFilter;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.ReceiptFilter;
import vn.viettel.sale.service.dto.*;

import java.util.Date;


public interface ReceiptExportService {

    /////////////////////////////////////////////////////////////////////////////////// Crud
    Response<Page<ReceiptImportListDTO>> find(String redInvoiceNo, Date fromDate, Date toDate, Integer type,Long shopId, Pageable pageable);
    Response<Object> createReceipt(ReceiptExportCreateRequest request, Long userId, Long shopId);
    Response<Object> updateReceiptExport(ReceiptExportUpdateRequest request, Long id);
    Response<String> removeReceiptExport(ReceiptExportUpdateRequest request,Long id);
    ////////////////////////////////////////////////////////////////////////////////// get for update
    Response<Page<PoTransDTO>> getListPoTrans( String transCode, String redInvoiceNo, String internalNumber, String poNo, Date fromDate, Date toDate, Pageable pageable);

}
