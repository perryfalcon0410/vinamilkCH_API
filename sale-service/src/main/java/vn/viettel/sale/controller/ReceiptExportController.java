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
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.dto.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sale/export")
public class ReceiptExportController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    ReceiptExportService receiptExportService;
    @RoleAdmin
    @GetMapping
    public Response<Page<ReceiptImportListDTO>> find(
             @RequestParam(value = "redInvoiceNo",required = false) String redInvoiceNo,
             @RequestParam(value = "fromDate",required = false) Date fromDate,
             @RequestParam(value = "toDate",required = false) Date toDate,
             @RequestParam(value = "type",required = false) Integer type,
             Pageable pageable) {
        return receiptExportService.find(redInvoiceNo,fromDate,toDate,type,this.getShopId(),pageable);
    }
    @RoleAdmin
    @PostMapping("/create")
    public Response<Object> createReceipt( @RequestBody ReceiptExportCreateRequest request) {
        return receiptExportService.createReceipt(request, this.getUserId(),this.getShopId());
    }
    @RoleAdmin
    @PatchMapping("/update/{Id}")
    public Response<Object> updateReceiptExport(@RequestBody ReceiptExportUpdateRequest request, @PathVariable long Id) {
        return receiptExportService.updateReceiptExport(request, Id);
    }
    @RoleAdmin
    @PutMapping("/remove/{Id}")
    public Response<String> removeReceiptExport(@RequestBody ReceiptExportUpdateRequest request,@PathVariable long Id) {
        return receiptExportService.removeReceiptExport(request,Id);
    }
    @RoleAdmin
    @GetMapping("/po-trans")
    public Response<Page<PoTransDTO>> getListPoTrans(@RequestParam(value = "transCode",required = false) String transCode,
                                                     @RequestParam(value = "redInvoiceNo",required = false) String redInvoiceNo,
                                                     @RequestParam(value = "internalNumber",required = false) String internalNumber,
                                                     @RequestParam(value = "poNo",required = false) String poNo,
                                                     @RequestParam(value = "fromDate",required = false ) Date fromDate,
                                                     @RequestParam(value = "toDate",required = false) Date toDate, Pageable pageable) {
        return receiptExportService.getListPoTrans(transCode,redInvoiceNo,internalNumber,poNo,fromDate,toDate,pageable);
    }
}
