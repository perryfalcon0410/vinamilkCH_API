package vn.viettel.saleservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.ReceiptImportRepository;
import vn.viettel.saleservice.service.ReceiptImportService;
import vn.viettel.saleservice.service.dto.ReceiptImportDTO;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/receipt-import")
public class ReceiptImportController {
    @Autowired
    ReceiptImportService receiptImportService;

    @GetMapping("/all")
    public Response<List<ReceiptImportDTO>> getAllCustomer(LocalDateTime fromDate, LocalDateTime toDate, String invoiceNumber, Integer type) {
        return receiptImportService.getAll(fromDate,toDate,invoiceNumber,type);
    }
}
