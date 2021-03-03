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
import vn.viettel.saleservice.service.dto.ReceiptCreateRequest;
import vn.viettel.saleservice.service.dto.ReceiptImportDTO;
import vn.viettel.saleservice.service.dto.ReceiptSearch;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/receipt-import")
public class ReceiptImportController {
    @Autowired
    ReceiptImportService receiptImportService;
    @GetMapping("/all")
    public Response<List<ReceiptImportDTO>> getReceiptImportByKeyWord(@RequestBody ReceiptSearch receiptSearch) {
        return receiptImportService.getAll(receiptSearch);
    }
    @GetMapping("/{reciId}")
    public Response<ReceiptImportDTO> getReceiptImportById(@PathVariable Long reciId) {
        return receiptImportService.getReceiptImportById(reciId);
    }
    @PostMapping("/create/{userId}")
    public Response<ReceiptImport> getReceiptImportById(@RequestBody ReceiptCreateRequest receiptCreateRequest, @PathVariable Long userId, @PathVariable Long idShop){
        return receiptImportService.createReceiptImport(receiptCreateRequest,userId,idShop);
    }
    @PutMapping("/update/{userId}")
    public Response<ReceiptImport> updateReceiptImport(@RequestBody ReceiptCreateRequest receiptCreateRequest, @PathVariable long userId) {
        return receiptImportService.updateReceiptImport(receiptCreateRequest, userId);
    }
    @DeleteMapping(value = "/all")
    public void deleteReceiptImport(@RequestBody long[] ids) {
        receiptImportService.remove(ids);
    }
}
