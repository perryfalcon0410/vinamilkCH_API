package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.ReceiptExport;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.ReceiptExportService;
import vn.viettel.saleservice.service.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class ReceiptExportController {
    @Autowired
    ReceiptExportService receiptExportService;
    @GetMapping("/recx-search")
    public Response<Page<ReceiptExportDTO>> getReceiptImportByKeyWord(@RequestBody ReceiptSearch receiptSearch, Pageable pageable ) {
        return receiptExportService.getReceiptExportBySearch(receiptSearch, pageable);
    }
    @GetMapping("/recx/{recxId}")
    public Response<ReceiptExportDTO> getReceiExportById(@PathVariable Long recxId) {
        return receiptExportService.getReceiptExportById(recxId);
    }
    @GetMapping("/receipt/export/adjusted")
    public Response<Page<ReceiptExportAdjustedDTO>> getAllExportAdjusted(Pageable pageable) {
        return receiptExportService.getAllReceiptExportAdjusted(pageable);
    }
    @GetMapping("/receipt/export/adjusted-detail/{Id}")
    public Response<List<ReceiptExportAdjustedDetailDTO>> getListExportAdjustedDetailByExportAdjustedId(Long Id) {
        return receiptExportService.getExportAdjustedDetailById(Id);
    }
    @GetMapping("/receipt/export/borrow")
    public Response<Page<ReceiptExportBorrowDTO>> getAllExportBorrow(Pageable pageable) {
        return receiptExportService.getAllReceiptExportBorrow(pageable);
    }
    @GetMapping("/receipt/export/borrow-detail/{Id}")
    public Response<List<ReceiptExportBorrowDetailDTO>> getListExportAdjustedDetailByExportBorrowId(Long Id) {
        return receiptExportService.getExportBorrowDetailById(Id);
    }
    @PostMapping("/create-recx/{userId}/{idShop}")
    public Response<ReceiptExport> createReceiptExport(@RequestBody ReceiptExportRequest rexr,@PathVariable Long userId, @PathVariable Long idShop) {
        return receiptExportService.createReceiptExport(rexr,userId,idShop);
    }
    @PutMapping("/update-recx/{userId}")
    public Response<ReceiptExport> updateReceiptImport(@RequestBody ReceiptExportRequest rexr, @PathVariable long userId) {
        return receiptExportService.updateReceiptExport(rexr, userId);
    }
}
