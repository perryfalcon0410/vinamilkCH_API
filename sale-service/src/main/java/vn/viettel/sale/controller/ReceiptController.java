package vn.viettel.sale.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.service.ReceiptService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.impl.ExportExcel;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sale/import")
public class ReceiptController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    ReceiptService receiptService;


    @GetMapping
    public Response<Page<ReceiptImportListDTO>> index(@RequestParam(value = "redInvoiceNo", required = false) String redInvoiceNo,@RequestParam(value = "fromDate", required = false) Date fromDate,@RequestParam(value = "toDate", required = false) Date toDate,@RequestParam(value = "type", required = false) Integer type,Pageable pageable) {
        return receiptService.index(redInvoiceNo,fromDate,toDate,type,pageable);
    }
    @PostMapping("/create")
    public Response<String> createReceipt(@Valid @RequestBody ReceiptCreateRequest request) {
        return receiptService.createReceipt(request,this.getUserId());
    }

    @GetMapping("/po-trans/{id}")
    public Response<PoTransDTO> getPoTrans(@PathVariable(name = "id") Long id) {
        return receiptService.getPoTransById(id);
    }

    @GetMapping("/adjustment-trans/{id}")
    public Response<StockAdjustmentTransDTO> getStockAdjustmentTrans(@PathVariable(name = "id") Long id) {
        return receiptService.getStockAdjustmentById(id);
    }

    @GetMapping("/borrow-trans/{id}")
    public Response<StockBorrowingTransDTO> getStockBorrowingTrans(@PathVariable(name = "id") Long id) {
        return receiptService.getStockBorrowingById(id);
    }
    @PatchMapping("/update/{Id}")
    public Response<String> updateReceiptImport(@RequestBody ReceiptUpdateRequest request, @PathVariable long Id) {
        return receiptService.updateReceiptImport(request, Id);
    }
    @PatchMapping("/remove/{Id}")
    public Response<String> removeReceiptImport(@RequestBody ReceiptUpdateRequest request, @PathVariable long Id) {
        return receiptService.removeReceiptImport(request, Id);
    }
    @GetMapping("/po-confirm")
    public Response<List<PoConfirmDTO>> getListPoConfirm() {
        return receiptService.getListPoConfirm();
    }
    @GetMapping("/stock-adjustment")
    public Response<List<StockAdjustmentDTO>> getListStockAdjustment() {
        return receiptService.getListStockAdjustment();
    }
    @GetMapping("/stock-borrowing")
    public Response<List<StockBorrowingDTO>> getListStockBorrowing() {
        return receiptService.getListStockBorrowing();
    }
    @GetMapping("/po-detail0/{id}")
    public Response<List<PoDetailDTO>> getPoDetailByPoId(@PathVariable Long id) {
        return receiptService.getPoDetailByPoId(id);
    }
    @GetMapping("/po-detail1/{id}")
    public Response<List<PoDetailDTO>> getPoDetailByPoIdAndPriceIsNull(@PathVariable Long id) {
        return receiptService.getPoDetailByPoIdAndPriceIsNull(id);
    }
    @GetMapping("/adjustment-detail/{id}")
    public Response<List<StockAdjustmentDetailDTO>> getStockAdjustmentDetail(@PathVariable Long id) {
        return receiptService.getStockAdjustmentDetail(id);
    }
    @GetMapping("/borrowing-detail/{id}")
    public Response<List<StockBorrowingDetailDTO>> getStockBorrowingDetail(@PathVariable Long id) {
        return receiptService.getStockBorrowingDetail(id);
    }
    @GetMapping("/po-trans-detail/{id}")
    public Response<List<PoTransDetailDTO>> getPoTransDetail(@PathVariable Long id) {
        return receiptService.getPoTransDetail(id);
    }
    @GetMapping("/adjustment-trans-detail/{id}")
    public Response<List<StockAdjustmentTransDetailDTO>> getStockAdjustmentTransDetail(@PathVariable Long id) {
        return receiptService.getStockAdjustmentTransDetail(id);
    }
    @GetMapping("/borrowing-trans-detail/{id}")
    public Response<List<StockBorrowingTransDetailDTO>> getStockBorrowingTransDetail(@PathVariable Long id) {
        return receiptService.getStockBorrowingTransDetail(id);
    }
    @PutMapping("/not-import/{Id}")
    public Response<String> setNotImport(@PathVariable long Id) {
        return receiptService.setNotImport(Id);
    }
    @GetMapping("/export/excel/{poId}")
    public ResponseEntity exportToExcel(@PathVariable Long poId) throws IOException {

        List<PoDetailDTO> soConfirmList = receiptService.getPoDetailByPoId(poId).getData();
        List<PoDetailDTO> soConfirmList2 = receiptService.getPoDetailByPoIdAndPriceIsNull(poId).getData();
        ExportExcel exportExcel = new ExportExcel(soConfirmList,soConfirmList2);
        ByteArrayInputStream in = exportExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=PoDetail.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
