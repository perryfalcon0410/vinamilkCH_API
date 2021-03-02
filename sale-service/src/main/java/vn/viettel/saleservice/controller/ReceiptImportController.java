package vn.viettel.saleservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.ReceiptImportRepository;
import vn.viettel.saleservice.service.ReceiptImportService;
import vn.viettel.saleservice.service.dto.ReceiptImportDTO;

import java.util.List;

@RestController
@RequestMapping("/api/receipt-import")
public class ReceiptImportController {
    Logger logger = LoggerFactory.getLogger(getClass());

    ReceiptImportService receiptImportService;

    public ReceiptImportController(ReceiptImportService receiptImportService) {
        this.receiptImportService = receiptImportService;
    }




}
