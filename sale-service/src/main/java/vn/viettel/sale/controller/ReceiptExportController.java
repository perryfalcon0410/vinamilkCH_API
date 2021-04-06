package vn.viettel.sale.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.dto.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class ReceiptExportController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    ReceiptExportService receiptExportService;

    @GetMapping("/index-export")
    public Response<Page<ReceiptImportListDTO>> index(@RequestParam(value = "redInvoiceNo", required = false) String redInvoiceNo, @RequestParam(value = "fromDate", required = false) Date fromDate, @RequestParam(value = "toDate", required = false) Date toDate, @RequestParam(value = "type", required = false) Integer type, Pageable pageable) {
        return receiptExportService.index(redInvoiceNo,fromDate,toDate,type,pageable);
    }

    @RoleAdmin
    @PostMapping("/create-export")
    public Response<String> createReceipt(@Valid @RequestBody ReceiptExportCreateRequest request) {
        return receiptExportService.createReceipt(request, this.getUserId());
    }
    /*@RoleFeign
    @RoleAdmin
    @GetMapping("/po-trans-export/{id}")
    public Response<PoTransDTO> getPoTransExport(@PathVariable(name = "id") Long id) {
        return receiptExportService.getPoTransExportById(id);
    }
    @RoleFeign
    @RoleAdmin
    @GetMapping("/adjustment-trans-export/{id}")
    public Response<StockAdjustmentTransDTO> getAdjustmentTransExport(@PathVariable(name = "id") Long id) {
        return receiptExportService.getStockAdjustmentTransById(id);
    }
    @RoleFeign
    @RoleAdmin
    @GetMapping("/borrowing-trans-export/{id}")
    public Response<StockBorrowingTransDTO> getBorrowingTransExport(@PathVariable(name = "id") Long id) {
        return receiptExportService.getStockBorrowingTransById(id);
    }
    @GetMapping("/stock-adjustment-export")
    public Response<List<StockAdjustmentDTO>> getListStockAdjustmentExport() {
        return receiptExportService.getListStockAdjustmentExport();
    }
    @GetMapping("/stock-borrowing-export")
    public Response<List<StockBorrowingDTO>> getListStockBorrowingExport() {
        return receiptExportService.getListStockBorrowingExport();
    }*/
    @PutMapping("/update-po-trans-export/{Id}")
    public Response<String> updatePoTransExport(@RequestBody ReceiptExportUpdateRequest request, @PathVariable long Id) {
        return receiptExportService.updatePoTransExport(request, Id);
    }
    @PutMapping("/update-adjustment-trans-export/{Id}")
    public Response<String> updateStockAdjustmentTrans(@RequestBody ReceiptExportUpdateRequest request, @PathVariable long Id) {
        return receiptExportService.updateStockAdjustmentTransExport(request, Id);
    }
    @PutMapping("/update-borrowing-trans-export/{Id}")
    public Response<String> updateBorrowingTransExport(@RequestBody ReceiptExportUpdateRequest request, @PathVariable long Id) {
        return receiptExportService.updateStockBorrowingTransExport(request, Id);
    }
    @PutMapping("/remove-po-trans-export/{Id}")
    public Response<String> removePoTransExport(@PathVariable long Id) {
        return receiptExportService.removePoTransExport(Id);
    }
    @PutMapping("/remove-adjustment-trans-export/{Id}")
    public Response<String> removeStockAdjustmentTransExport(@PathVariable long Id) {
        return receiptExportService.removeStockAdjustmentTransExport(Id);
    }
    @PutMapping("/remove-borrowing-trans-export/{Id}")
    public Response<String> removeBorrowingTransExport(@PathVariable long Id) {
        return receiptExportService.removeStockBorrowingTransExport(Id);
    }
    @GetMapping("/po-trans-export")
    public Response<Page<PoTransDTO>> getListPoTrans(@RequestParam(value = "transCode",required = false) String transCode,@RequestParam(value = "redInvoiceNo",required = false) String redInvoiceNo,@RequestParam(value = "internalNumber",required = false) String internalNumber,@RequestParam(value = "poNo",required = false) String poNo,@RequestParam(value = "fromDate",required = false) Date fromDate, @RequestParam(value = "toDate",required = false) Date toDate, Pageable pageable) {
        return receiptExportService.getListPoTrans(transCode,redInvoiceNo,internalNumber,poNo,fromDate,toDate,pageable);
    }
}
