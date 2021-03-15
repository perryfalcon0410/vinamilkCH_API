package vn.viettel.saleservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.ReceiptExport;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.ReceiptExportDTO;
import vn.viettel.saleservice.service.dto.ReceiptExportRequest;
import vn.viettel.saleservice.service.dto.ReceiptSearch;
public interface ReceiptExportService {
    Response<Page<ReceiptExportDTO>> getReceiptExportBySearch(ReceiptSearch receiptSearch, Pageable pageable);
    Response<ReceiptExport> createReceiptExport(ReceiptExportRequest rexr, long userId, long idShop);
    String createReceiptExportCode(Long idShop);
}
