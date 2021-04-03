package vn.viettel.sale.controller;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.sale.service.impl.InvoiceReportService;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/sale")
public class InvoiceReportController extends BaseController {

    @Autowired
    InvoiceReportService invoiceReportService;

    /// invoiceType: 0 - Trả hàng PO, 1 - Xuất điều chỉnh, 2 - xuất vay mượn
    @GetMapping(value = "/report/invoice_export.pdf")
    public ResponseEntity exportInvoiceExport(@RequestParam("invoiceId") Long invoiceId
                                             ,@RequestParam("invoiceType") Integer invoiceType) throws FileNotFoundException, JRException{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invoice_export.pdf");
        ByteArrayInputStream inputStream = invoiceReportService.testInvoice(invoiceId, invoiceType);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inputStream));
    }


}
