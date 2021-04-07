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
    public Response<Page<ReceiptImportListDTO>> find(@RequestBody ReceiptFilter filter, Pageable pageable) {
        return receiptExportService.find(filter,pageable);
    }
    @RoleAdmin
    @PostMapping("/create")
    public Response<Object> createReceipt( @RequestBody ReceiptExportCreateRequest request) {
        return receiptExportService.createReceipt(request, this.getUserId());
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
    public Response<Page<PoTransDTO>> getListPoTrans(@RequestBody PoTransFilter filter, Pageable pageable) {
        return receiptExportService.getListPoTrans(filter,pageable);
    }
}
