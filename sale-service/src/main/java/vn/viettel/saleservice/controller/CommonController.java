package vn.viettel.saleservice.controller;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.CommonService;
import vn.viettel.saleservice.service.dto.ReasonDTO;
import vn.viettel.saleservice.service.dto.ReceiptImportDTO;
import vn.viettel.saleservice.service.dto.ReceiptSearch;
import vn.viettel.saleservice.service.dto.ShopDTO;
import vn.viettel.saleservice.service.impl.InvoiceReport;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class CommonController {
    @Autowired
    CommonService commonService;
    @Autowired
    InvoiceReport invoiceReport;
    @GetMapping("/reason")
    public Response<List<ReasonDTO>> getAllReason() {
        return commonService.getAllReason();
    }
    @GetMapping("/shop/{shopId}")
    public Response<ShopDTO> getShopById(@PathVariable Long shopId) {
        return commonService.getShopById(shopId);
    }
    @GetMapping("/report/{format}/{idRe}")
    public String exportReport(@PathVariable String format,@PathVariable Long idRe) throws FileNotFoundException, JRException {
        return invoiceReport.exportReport(format,idRe);
    }
}
