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
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.excel.ExportExcel;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
public class ReceiptImportController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    ReceiptImportService receiptService;
    private final String root = "/sales/import";

    @RoleAdmin
    @GetMapping(value = { V1 + root })
    public Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>> find(
                                @RequestParam(value ="redInvoiceNo", required = false ) String redInvoiceNo,
                                @RequestParam(value ="fromDate", required = false ) Date fromDate,
                                @RequestParam(value ="toDate", required = false ) Date toDate,
                                @RequestParam(value ="type", required = false ) Integer type, Pageable pageable) {
        return receiptService.find(redInvoiceNo,fromDate,toDate,type,this.getShopId(),pageable);
    }

    @RoleAdmin
    @PostMapping(value = { V1 + root })
    public Response<Object> createReceipt(@Valid @RequestBody ReceiptCreateRequest request) {
        return receiptService.createReceipt(request,this.getUserId(),this.getShopId());
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/trans/{id}"})
    public Response<Object> getTrans(@PathVariable(name = "id") Long id,@RequestParam Integer type) {
        return receiptService.getForUpdate(type,id);
    }

    @RoleAdmin
    @PatchMapping(value = { V1 + root + "/update/{id}"})
    public Response<Object> updateReceiptImport(@RequestBody ReceiptUpdateRequest request, @PathVariable long id) {
        return receiptService.updateReceiptImport(request, id);
    }

    @RoleAdmin
    @PatchMapping(value = { V1 + root + "/remove/{Id}"})
    public Response<String> removeReceiptImport(@PathVariable long id,@RequestParam Integer type ) {
        return receiptService.removeReceiptImport( id,type);
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/po-confirm"})
    public Response<List<PoConfirmDTO>> getListPoConfirm() {
        return receiptService.getListPoConfirm();
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/adjustment"})
    public Response<List<StockAdjustmentDTO>> getListStockAdjustment() {
        return receiptService.getListStockAdjustment();
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/borrowing"})
    public Response<List<StockBorrowingDTO>> getListStockBorrowing() {
        return receiptService.getListStockBorrowing(this.getShopId());
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/po-detail0/{id}"})
    public Response<CoverResponse<List<PoDetailDTO>,TotalResponse>> getPoDetailByPoId(@PathVariable Long id) {
        return receiptService.getPoDetailByPoId(id,this.getShopId());
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/po-detail1/{id}"})
    public Response<CoverResponse<List<PoDetailDTO>,TotalResponse>> getPoDetailByPoIdAndPriceIsNull(@PathVariable Long id) {
        return receiptService.getPoDetailByPoIdAndPriceIsNull(id,this.getShopId());
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/adjustment-detail/{id}"})
    public Response<List<StockAdjustmentDetailDTO>> getStockAdjustmentDetail(@PathVariable Long id) {
        return receiptService.getStockAdjustmentDetail(id);
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/borrowing-detail/{id}"})
    public Response<List<StockBorrowingDetailDTO>> getStockBorrowingDetail(@PathVariable Long id) {
        return receiptService.getStockBorrowingDetail(id);
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "trans-detail/{id}"})
    public Response<Object> getPoTransDetail(@PathVariable Long id, @RequestParam Integer type) {
        return receiptService.getTransDetail(type,id,this.getShopId());
    }

    @RoleAdmin
    @PutMapping(value = { V1 + root + "/not-import/{Id}"})
    public Response<String> setNotImport(@PathVariable long Id) {
        return receiptService.setNotImport(Id);
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/excel/{poId}"})
    public ResponseEntity exportToExcel(@PathVariable Long poId) throws IOException {

        CoverResponse<List<PoDetailDTO>,TotalResponse> soConfirmList = receiptService.getPoDetailByPoId(poId,this.getShopId()).getData();
        List<PoDetailDTO> list1 = soConfirmList.getResponse();
        CoverResponse<List<PoDetailDTO>,TotalResponse> soConfirmList2 = receiptService.getPoDetailByPoIdAndPriceIsNull(poId,this.getShopId()).getData();
        List<PoDetailDTO> list2 = soConfirmList2.getResponse();
        ExportExcel exportExcel = new ExportExcel(list1,list2);
        ByteArrayInputStream in = exportExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=PoDetail.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
