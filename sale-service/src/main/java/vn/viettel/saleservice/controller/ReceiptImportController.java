package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.ReceiptImportService;
import vn.viettel.saleservice.service.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipt-import")
public class ReceiptImportController {
    @Autowired
    ReceiptImportService receiptImportService;
    @GetMapping("/all")
    public Response<Page<ReceiptImportDTO>> getAll(Pageable pageable) {
        return receiptImportService.getAll(pageable);
    }
    @GetMapping("/search")
    public Response<Page<ReceiptImportDTO>> getReceiptImportByKeyWord(@RequestBody ReceiptSearch receiptSearch,Pageable pageable ) {
        return receiptImportService.getReceiptImportBySearch(receiptSearch, pageable);
    }
    @GetMapping("/any/search")
    public Response<Page<ReceiptImportDTO>> getAnyReceiptImportByKeyWord(@RequestBody ReceiptSearch receiptSearch,Pageable pageable ) {
        return receiptImportService.getAnyReceiptImportBySearch(receiptSearch, pageable);
    }
    @GetMapping("detail/{reciId}")
    public Response<List<ReceiptImportDetailDTO>> getReceiptImportDetailByReciId(@PathVariable Long reciId) {
        return receiptImportService.getReceiptImportDetailByReciId(reciId);
    }
    @GetMapping("/{reciId}")
    public Response<ReceiptImportDTO> getReceiptImportById(@PathVariable Long reciId) {
        return receiptImportService.getReceiptImportById(reciId);
    }
    @PostMapping("/create/{userId}/{idShop}")
    public Response<ReceiptImport> createReceiptImportById(@RequestBody POPromotionalRequest pro, @PathVariable Long userId, @PathVariable Long idShop){
        return receiptImportService.createReceiptImport(pro,userId,idShop);
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
