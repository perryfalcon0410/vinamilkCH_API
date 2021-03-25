package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.db.entity.stock.StockAdjustmentTrans;
import vn.viettel.core.db.entity.stock.StockBorrowingTrans;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.PoTransCreateRequest;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.StockAdjustmentRequest;
import vn.viettel.sale.messaging.StockBorrowingRequest;
import vn.viettel.sale.service.dto.*;

import java.util.Date;

public interface ReceiptService {
    Response<Page<PoTransDTO>> getAll(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Pageable pageable);
    public Response<String> createReceipt(ReceiptCreateRequest request,Long shopId,Long userId);
}