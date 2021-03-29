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
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.service.ReceiptService;
import vn.viettel.sale.service.dto.*;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/api/sale")
public class ReceiptController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    ReceiptService receiptService;

    /**
     *
     * @param redInvoiceNo search full name or customer code
     * @param fromDate default start date of month
     * @param toDate default last date of month
     * @param type customer groups
     * @param pageable size, page
     * @return Response<Page<CustomerDTO>>>
     */
    @RoleAdmin
    @GetMapping("/reci-all")
    public Response<Page<PoTransDTO>> getAll(@RequestParam String redInvoiceNo, @RequestParam Date fromDate, @RequestParam Date toDate , @RequestParam Integer type, Pageable pageable) {
        logger.info("[index()] - poTrans index #user_id: {}, #redInvoiceNo: {}", this.getUserId(), redInvoiceNo);
        return receiptService.getAll(redInvoiceNo,fromDate,toDate,type,pageable);
    }
    @RoleAdmin
    @PostMapping("/create")
    public Response<String> createReceipt(@Valid @RequestBody ReceiptCreateRequest request,Long shopId,Long userId) {
        return receiptService.createReceipt(request,shopId,userId);
    }
}