package vn.viettel.sale.controller;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.InvoiceReportService;
import vn.viettel.sale.service.dto.ComboProductDTO;
import vn.viettel.sale.service.dto.ReportProductTransDTO;
import vn.viettel.sale.service.impl.ReportProductTransImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/sale/report")
public class InvoiceReportController extends BaseController {

    @Autowired
    InvoiceReportService invoiceReportService;

    @Autowired
    ReportProductTransImpl reportProductTrans;

    /**
     * @param transCode: EXSP/EXST/EXSB
     * @return ResponseEntity<InputStreamResource>
     */
    @RoleAdmin
    @GetMapping(value = "/stock/invoice")
    public ResponseEntity invoiceReport(@RequestParam("transCode") String transCode) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invoice_export.pdf");
        ByteArrayInputStream inputStream = invoiceReportService.invoiceReport(this.getShopId(), transCode);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inputStream));
    }

    @RoleAdmin
    @GetMapping("/{transCode}")
    public Response<ReportProductTransDTO> findComboProducts(@PathVariable String transCode) {
        return reportProductTrans.getReport(this.getShopId(), transCode);
    }


}
