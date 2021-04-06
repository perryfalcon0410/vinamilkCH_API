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
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.impl.InvoiceReportService;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/sale")
public class InvoiceReportController extends BaseController {

    @Autowired
    InvoiceReportService invoiceReportService;

    /**
     * @param shopId: id shop
     * @param transCode: mã giao dịch
     * @return ResponseEntity<InputStreamResource>
     */
    //@RoleAdmin
    @GetMapping(value = "/report/invoice")
    public ResponseEntity invoiceReport(@RequestParam("shopId") Long shopId,
                                        @RequestParam("transCode") String transCode) throws FileNotFoundException, JRException{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invoice_exportv2.pdf");
        ByteArrayInputStream inputStream = invoiceReportService.invoiceReport(shopId , transCode);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inputStream));
    }


}
