package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.ReceiptExportService;
import vn.viettel.saleservice.service.dto.ReceiptExportDTO;
import vn.viettel.saleservice.service.dto.ReceiptImportDTO;
import vn.viettel.saleservice.service.dto.ReceiptSearch;

@RestController
@RequestMapping("/api/receipt-export")
public class ReceiptExportController {
    @Autowired
    ReceiptExportService receiptExportService;
    @GetMapping("/search")
    public Response<Page<ReceiptExportDTO>> getReceiptImportByKeyWord(@RequestBody ReceiptSearch receiptSearch, Pageable pageable ) {
        return receiptExportService.getReceiptExportBySearch(receiptSearch, pageable);
    }
}
