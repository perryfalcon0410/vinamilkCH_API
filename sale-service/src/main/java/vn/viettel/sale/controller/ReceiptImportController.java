package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class ReceiptImportController {
    @Autowired
    ReceiptImportService receiptImportService;
    @GetMapping("/reci-all")
    public Response<Page<ReceiptImportDTO>> getAll(Pageable pageable) {
        return receiptImportService.getAll(pageable);
    }
    @GetMapping("/reci-search")
    public Response<Page<ReceiptImportDTO>> getReceiptImportByKeyWord(@RequestParam String invoiceNumber, @RequestParam String fromDate,@RequestParam String toDate ,@RequestParam Integer type, Pageable pageable ) {
        return receiptImportService.getReceiptImportBySearch(invoiceNumber,fromDate,toDate,type, pageable);
    }
    @GetMapping("/reci-any-search")
    public Response<Page<ReceiptImportDTO>> getAnyReceiptImportByKeyWord(@RequestBody ReceiptSearch receiptSearch,Pageable pageable ) {
        return receiptImportService.getAnyReceiptImportBySearch(receiptSearch, pageable);
    }
    @GetMapping("/reci-detail/{reciId}")
    public Response<List<ReceiptImportDetailDTO>> getReceiptImportDetailByReciId(@PathVariable Long reciId) {
        return receiptImportService.getReceiptImportDetailByReciId(reciId);
    }
    @GetMapping("/reci/{reciId}")
    public Response<ReceiptImportDTO> getReceiptImportById(@PathVariable Long reciId) {
        return receiptImportService.getReceiptImportById(reciId);
    }
    @PostMapping("/create-reci/{userId}/{idShop}")
    public Response<ReceiptImport> createReceiptImportById(@RequestBody POPromotionalRequest pro, @PathVariable Long userId, @PathVariable Long idShop){
        return receiptImportService.createReceiptImport(pro,userId,idShop);
    }
    @PutMapping("/update-reci/{userId}")
    public Response<ReceiptImport> updateReceiptImport(@RequestBody ReceiptCreateRequest receiptCreateRequest, @PathVariable long userId) {
        return receiptImportService.updateReceiptImport(receiptCreateRequest, userId);
    }
    @DeleteMapping(value = "/reci-all")
    public void deleteReceiptImport(@RequestBody long[] ids) {
        receiptImportService.remove(ids);
    }
}
